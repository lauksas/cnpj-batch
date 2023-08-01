package com.mux.cnpj.batch.job.step;

import static com.mux.cnpj.batch.formatter.CsvFormatter.fromCsvString;
import static com.mux.cnpj.batch.formatter.CsvFormatter.intAsText;
import static com.mux.cnpj.batch.formatter.CsvFormatter.nullIfEmpty;
import static com.mux.cnpj.batch.formatter.CsvFormatter.telToInt;
import static com.mux.cnpj.batch.formatter.CsvFormatter.toInteger;

import java.time.LocalDateTime;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import com.mux.cnpj.batch.data.entity.Cnae;
import com.mux.cnpj.batch.data.entity.CnpjId;
import com.mux.cnpj.batch.data.entity.Estabilishment;
import com.mux.cnpj.batch.data.entity.Municipality;
import com.mux.cnpj.batch.dto.EstablishmentCsv;
import com.mux.cnpj.batch.job.step.factory.AbstractCNPJStepBuilder;

@Component
public class EstabilishmentsImportStepBuilder extends AbstractCNPJStepBuilder<EstablishmentCsv, Estabilishment> {

	private static final String RJ_CITY_CODE = "6001";
	private static final String ACTIVE_CODE = "02";

	@Override
	public ItemProcessor<EstablishmentCsv, Estabilishment> getProcessor() {
		return new ItemProcessor<EstablishmentCsv, Estabilishment>() {
			@Override
			@Nullable
			public Estabilishment process(@NonNull EstablishmentCsv csv) throws Exception {
				String statusId = csv.getSituacaoCadastral_colF_6().trim();

				if (!ACTIVE_CODE.equals(statusId))
					return null;

				String cityCode = csv.getCodMunicipio_colU_21().trim();

				if (!RJ_CITY_CODE.equals(cityCode))
					return null;

				Estabilishment estabilishment = Estabilishment.builder()
						.cnpjId(
								CnpjId.builder().cnpj(
										toInteger(csv.getCnpj_colA_1()))
										.headquartersPart(toInteger(csv.getFilial_colB_2()))
										.checkDigit(toInteger(csv.getDigitoVerificador_colC_3()))
										.build())
						.headquartersIndicator(toInteger(csv.getIndMatrizFilialRepetido_colD_4()))
						.tradeName(nullIfEmpty(csv.getNomeFantasia_colE_5()))
						.statusId(toInteger(statusId))
						.mainCnaeFiscal(
								Cnae.builder()
										.id(toInteger(csv.getCnaeFiscalPrincipal_colL_12()))
										.build())
						.fiscalCenae(fromCsvString(csv
								.getCnaeFiscal_colM_13()))
						.streetType(nullIfEmpty(csv.getTipoLograodouro_colN_14()))
						.street(nullIfEmpty(csv.getLogradouro_colO_15()))
						.streetNumber(nullIfEmpty(intAsText(csv.getNumero_colP_16())))
						.complement(nullIfEmpty(csv.getComplemento_colQ_17()))
						.district(nullIfEmpty(csv.getBairro_colR_18()))
						.zipCode(telToInt(csv.getCep_colS_19()))
						.stateCode(nullIfEmpty(csv.getUf_colT_20()))
						.cityCode(
								Municipality.builder()
										.id(toInteger(cityCode))
										.build())
						.areaCode1(toInteger(csv.getDdd1_colV_22()))
						.telephone1(telToInt(csv.getTel1_colW_23()))
						.areaCode2(telToInt(csv.getDdd2_colV_24()))
						.telephone2(telToInt(csv.getDdd2_colV_24()))
						.faxAreaCode(telToInt(csv.getFax_colV_26()))
						.faxTelephone(telToInt(csv.getFax_colW_27()))
						.lastUpdated(LocalDateTime.now())
						.build();

				return estabilishment;
			}
		};
	}

	@Override
	public Class<EstablishmentCsv> getCsvClass() {
		return EstablishmentCsv.class;
	}

	@Override
	public String getFilePattern() {
		return "Estabelecimentos*.zip";
	}

	@Override
	public String[] getColumnNames() {
		return new String[] {
				"cnpj_colA_1",
				"filial_colB_2",
				"digitoVerificador_colC_3",
				"indMatrizFilialRepetido_colD_4",
				"nomeFantasia_colE_5",
				"situacaoCadastral_colF_6",
				"cnaeFiscalPrincipal_colL_12",
				"cnaeFiscal_colM_13",
				"tipoLograodouro_colN_14",
				"logradouro_colO_15",
				"numero_colP_16",
				"complemento_colQ_17",
				"bairro_colR_18",
				"cep_colS_19",
				"uf_colT_20",
				"codMunicipio_colU_21",
				"ddd1_colV_22",
				"tel1_colW_23",
				"ddd2_colV_24",
				"tel3_colW_25",
				"fax_colV_26",
				"fax_colW_27",
				"correioEletronico_colAB_28",
		};
	}

	@Override
	public int[] getIncludeColumns() {
		return new int[] {
				0,
				1,
				2,
				3,
				4,
				5,
				11,
				12,
				13,
				14,
				15,
				16,
				17,
				18,
				19,
				20,
				21,
				22,
				23,
				24,
				25,
				26,
				27
		};
	}

}
