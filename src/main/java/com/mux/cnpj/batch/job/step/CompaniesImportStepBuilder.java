package com.mux.cnpj.batch.job.step;

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
import com.mux.cnpj.batch.data.entity.LegalNature;
import com.mux.cnpj.batch.data.entity.Reason;
import com.mux.cnpj.batch.dto.CompanyCsv;
import com.mux.cnpj.batch.job.step.factory.AbstractCNPJStepBuilder;
import com.mux.cnpj.batch.job.writer.JdbcBatchItemWriterCNPJ;
import com.mux.cnpj.config.DataSourceConfig;

@Component
public class CompaniesImportStepBuilder extends AbstractCNPJStepBuilder<CompanyCsv, Company> {

	private static final String query = """
					INSERT INTO cnpj.company
						(cnpj, close_down_reason_id, company_size, legal_nature_id, name, social_capital)
						select :cnpj, :closeDownReasonId, :companySize, :legalNatureId, :name, :socialCapital
						where exists
							(select 1 from cnpj.establishment es where es.cnpj=:cnpj limit 1)
					on conflict (cnpj)
						do update set
						cnpj = :cnpj,
						close_down_reason_id = :closeDownReasonId,
						company_size = :companySize,
						legal_nature_id = :legalNatureId,
						name = :name,
						social_capital = :socialCapital
					;
			""";

	@Override
	public ItemProcessor<CompanyCsv, Company> getProcessor() {
		return new ItemProcessor<CompanyCsv, Company>() {
			@Override
			@Nullable
			public Company process(@NonNull CompanyCsv csv) throws Exception {

				Company company = Company.builder()
						.cnpj(toInteger(csv.getCnpj_col1_A()))
						.name(csv.getNome_col2_B().trim())
						.legalNature(
								LegalNature.builder()
										.id(toInteger(csv.getCodNaturezaJuridica_col3_C()))
										.build())
						.closeDownReason(
								Reason.builder()
										.id(toInteger(csv.getMotivoSituacaoCadastral_col4_D()))
										.build())
						.socialCapital(toBigDecimal(csv.getSocialCapital_col5_E()))
						.companySize(toInteger(csv.getCodPorteEmpresa_col6_F()))
						.build();

				return company;
			}
		};
	}

	@Override
	public Class<CompanyCsv> getCsvClass() {
		return CompanyCsv.class;
	}

	@Override
	public String getFilePattern() {
		return "Empresas*.zip";
	}

	@Override
	public String[] getColumnNames() {
		return new String[] {
				"cnpj_col1_A",
				"nome_col2_B",
				"codNaturezaJuridica_col3_C",
				"motivoSituacaoCadastral_col4_D",
				"socialCapital_col5_E",
				"codPorteEmpresa_col6_F",
				"estadoEUnidadeFederativa_col7_G"
		};
	}

	@Override
	public int[] getIncludeColumns() {
		return new int[] { 0, 1, 2, 3, 4, 5, 6 };
	}

	@Autowired
	@Qualifier(DataSourceConfig.CNPJ_DATASOURCE_BEAN_NAME)
	DataSource cnpjDataSource;

	@Override
	public ItemWriter<Company> getWriter() {
		JdbcBatchItemWriterCNPJ<Company> writer = new JdbcBatchItemWriterCNPJ<>();
		writer.setDataSource(cnpjDataSource);
		writer.setAssertUpdates(false);
		writer.setSql(query);
		writer.setUsingNamedParameters(true);
		writer.setItemSqlParameterSourceProvider(new ItemSqlParameterSourceProvider<Company>() {

			@Override
			public SqlParameterSource createSqlParameterSource(Company item) {
				Map<String, Object> map = new HashMap<>();

				Integer closeDownReasonId = item.getCloseDownReason() != null ? item.getCloseDownReason().getId() : null;

				map.put("cnpj", item.getCnpj());
				map.put("closeDownReasonId", closeDownReasonId);
				map.put("companySize", item.getCompanySize());
				map.put("legalNatureId", item.getLegalNature().getId());
				map.put("name", item.getName());
				map.put("socialCapital", item.getSocialCapital());

				return new MapSqlParameterSource(map);
			}

		});

		return writer;
	}

}
