package com.mux.cnpj.batch.job.step;

import static com.mux.cnpj.batch.formatter.CsvFormatter.nullIfEmpty;
import static com.mux.cnpj.batch.formatter.CsvFormatter.toBigDecimal;
import static com.mux.cnpj.batch.formatter.CsvFormatter.toInteger;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.ItemSqlParameterSourceProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import com.mux.cnpj.batch.data.entity.Company;
import com.mux.cnpj.batch.data.entity.Country;
import com.mux.cnpj.batch.data.entity.Partner;
import com.mux.cnpj.batch.data.entity.PartnerQualification;
import com.mux.cnpj.batch.data.entity.PersonType;
import com.mux.cnpj.batch.dto.PartnersCsv;
import com.mux.cnpj.batch.job.step.factory.AbstractCNPJStepBuilder;
import com.mux.cnpj.batch.job.writer.JdbcBatchItemWriterCNPJ;
import com.mux.cnpj.config.DataSourceConfig;

@Component
public class PartnersImportStepBuilder extends AbstractCNPJStepBuilder<PartnersCsv, Partner> {

	private static final String query = """
					INSERT INTO cnpj.partner
						(
							company_cnpj,
							id,
							person_type_id,
							name,
							masked_cpf_or_cnpj,
							qualification_id,
							country_id,
							legal_representant_masked_cpf,
							legal_representant_name,
							legal_representant_qualification_id
						)
						select
							:companyCnpj,
							:id,
							:personTypeId,
							:name,
							:maskedCpfOrCnpj,
							:qualificationId,
							:countryId,
							:legalRepresentantMaskedCpf,
							:legalRepresentantName,
							:legalRepresentantQualificationId
						where exists
							(select 1 from cnpj.estabilishment es where es.cnpj=:companyCnpj limit 1)
					on conflict (id)
						do update set
							company_cnpj = :companyCnpj,
							person_type_id = :personTypeId,
							name = :name,
							masked_cpf_or_cnpj = :maskedCpfOrCnpj,
							qualification_id = :qualificationId,
							country_id = :countryId,
							legal_representant_masked_cpf = :legalRepresentantMaskedCpf,
							legal_representant_name = :legalRepresentantName,
							legal_representant_qualification_id = :legalRepresentantQualificationId
					;
			""";

	@Override
	public ItemProcessor<PartnersCsv, Partner> getProcessor() {
		return new ItemProcessor<PartnersCsv, Partner>() {
			@Override
			@Nullable
			public Partner process(@NonNull PartnersCsv csv) throws Exception {

				Integer companyCnpj = toInteger(csv.getCnpj_colA_1());

				String masketCpfOrCnpjString = csv.getCnpjSocioOuCpfMascarado_colD_4()
						.replaceAll("\\*", "");

				Country country = Country.builder().id(
						toInteger(csv.getCodPaisSocioEstrangeiro_colG_7()))
						.build();

				Partner partner = Partner.builder()
						.maskedCpfOrCnpj(
								toBigDecimal(masketCpfOrCnpjString))
						.company(Company.builder().cnpj(companyCnpj).build())
						.personType(
								PersonType.builder()
										.id(toInteger(csv.getTipoSocio_colB_2()))
										.build())
						.name(nullIfEmpty(csv.getNomeSocio_colC_3()))
						.qualification(
								PartnerQualification.builder()
										.id(toInteger(csv.getQualificacao_colE_5()))
										.build())
						.country(country)
						.legalRepresentantName(nullIfEmpty(csv.getNomeRepresentanteLegal_colI_9()))
						.legalRepresentantQualification(
								PartnerQualification.builder()
										.id(toInteger(csv.getQualificacaoRepresentanteLegal_colJ_10()))
										.build())
						.build()
						.craftId();

				setLegalRepresentantCpf(csv, partner);

				return partner;
			}

			private void setLegalRepresentantCpf(PartnersCsv csv, Partner partner) {
				String legalRepresentantCpfString = csv.getCpfMascaradoRepresentanteLegal_colH_8()
						.replaceAll("\\*", "");
				partner.setLegalRepresentantMaskedCpf(toInteger(legalRepresentantCpfString));
			}
		};
	}

	@Override
	public Class<PartnersCsv> getCsvClass() {
		return PartnersCsv.class;
	}

	@Override
	public String getFilePattern() {
		return "Socios*.zip";
	}

	@Override
	public String[] getColumnNames() {
		return new String[] {
				"cnpj_colA_1",
				"tipoSocio_colB_2",
				"nomeSocio_colC_3",
				"CnpjSocioOuCpfMascarado_colD_4",
				"qualificacao_colE_5",
				"codPaisSocioEstrangeiro_colG_7",
				"cpfMascaradoRepresentanteLegal_colH_8",
				"nomeRepresentanteLegal_colI_9",
				"qualificacaoRepresentanteLegal_colJ_10"
		};
	}

	@Override
	public int[] getIncludeColumns() {
		return new int[] { 0, 1, 2, 3, 4, 6, 7, 8, 9 };
	}

	@Autowired
	@Qualifier(DataSourceConfig.CNPJ_DATASOURCE_BEAN_NAME)
	DataSource cnpjDataSource;

	@Override
	public ItemWriter<Partner> getWriter() {
		JdbcBatchItemWriterCNPJ<Partner> writer = new JdbcBatchItemWriterCNPJ<>();
		writer.setDataSource(cnpjDataSource);
		writer.setAssertUpdates(false);
		writer.setSql(query);
		writer.setUsingNamedParameters(true);
		writer.setItemSqlParameterSourceProvider(new ItemSqlParameterSourceProvider<Partner>() {

			@Override
			public SqlParameterSource createSqlParameterSource(Partner item) {
				Map<String, Object> map = new HashMap<>();

				map.put("companyCnpj", item.getCompany().getCnpj());
				map.put("maskedCpfOrCnpj", item.getMaskedCpfOrCnpj());
				map.put("personTypeId", item.getPersonType().getId());
				map.put("name", item.getName());
				map.put("id", item.getId());
				map.put("qualificationId", item.getQualification().getId());
				map.put("countryId", item.getCountry().getId());
				map.put("legalRepresentantMaskedCpf", item.getLegalRepresentantMaskedCpf());
				map.put("legalRepresentantName", item.getLegalRepresentantName());
				map.put("legalRepresentantQualificationId", item.getLegalRepresentantQualification().getId());

				return new MapSqlParameterSource(map);
			}

		});

		return writer;
	}

}
