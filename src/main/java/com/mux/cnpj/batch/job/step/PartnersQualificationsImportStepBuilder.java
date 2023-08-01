package com.mux.cnpj.batch.job.step;

import static com.mux.cnpj.batch.formatter.CsvFormatter.toInteger;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import com.mux.cnpj.batch.data.entity.PartnerQualification;
import com.mux.cnpj.batch.dto.PartnerQualificationsCsv;
import com.mux.cnpj.batch.job.step.factory.AbstractCNPJStepBuilder;

@Component
public class PartnersQualificationsImportStepBuilder
		extends AbstractCNPJStepBuilder<PartnerQualificationsCsv, PartnerQualification> {

	@Override
	public ItemProcessor<PartnerQualificationsCsv, PartnerQualification> getProcessor() {
		return new ItemProcessor<PartnerQualificationsCsv, PartnerQualification>() {
			@Override
			@Nullable
			public PartnerQualification process(@NonNull PartnerQualificationsCsv csv) throws Exception {

				PartnerQualification cnae = PartnerQualification.builder()
						.id(toInteger(csv.getCodQualificacao_col1_a()))
						.description(csv.getDescricao_col2_b())
						.build();

				return cnae;
			}
		};
	}

	@Override
	public Class<PartnerQualificationsCsv> getCsvClass() {
		return PartnerQualificationsCsv.class;
	}

	@Override
	public String getFilePattern() {
		return "Qualificacoes.zip";
	}

	@Override
	public String[] getColumnNames() {
		return new String[] {
				"codQualificacao_col1_a",
				"descricao_col2_b",
		};
	}

	@Override
	public int[] getIncludeColumns() {
		return new int[] { 0, 1 };
	}

}
