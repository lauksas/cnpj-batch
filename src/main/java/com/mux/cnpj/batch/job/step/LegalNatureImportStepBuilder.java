package com.mux.cnpj.batch.job.step;

import static com.mux.cnpj.batch.formatter.CsvFormatter.toInteger;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.mux.cnpj.batch.data.entity.LegalNature;
import com.mux.cnpj.batch.dto.LegalNatureCsv;
import com.mux.cnpj.batch.job.step.factory.AbstractCNPJStepBuilder;

@Component
public class LegalNatureImportStepBuilder extends AbstractCNPJStepBuilder<LegalNatureCsv, LegalNature> {

	@Override
	public ItemProcessor<LegalNatureCsv, LegalNature> getProcessor() {
		return new ItemProcessor<LegalNatureCsv, LegalNature>() {

			@Override
			public LegalNature process(LegalNatureCsv item) throws Exception {
				return LegalNature.builder()
						.id(toInteger(item.getCodNaturezaLegal_col1_a()))
						.description(item.getDescricao_col2_b().trim())
						.build();
			}

		};
	}

	@Override
	public Class<LegalNatureCsv> getCsvClass() {
		return LegalNatureCsv.class;
	}

	@Override
	public String getFilePattern() {
		return "Naturezas.zip";
	}

	@Override
	public String[] getColumnNames() {
		return new String[] {
				"codNaturezaLegal_col1_a",
				"descricao_col2_b"
		};
	}

	@Override
	public int[] getIncludeColumns() {
		return new int[] { 0, 1 };
	}

}
