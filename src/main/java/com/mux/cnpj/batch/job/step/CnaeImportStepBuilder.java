package com.mux.cnpj.batch.job.step;

import static com.mux.cnpj.batch.formatter.CsvFormatter.toInteger;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import com.mux.cnpj.batch.data.entity.Cnae;
import com.mux.cnpj.batch.dto.CnaeCsv;
import com.mux.cnpj.batch.job.step.factory.AbstractCNPJStepBuilder;

@Component
public class CnaeImportStepBuilder extends AbstractCNPJStepBuilder<CnaeCsv, Cnae> {

	@Override
	public ItemProcessor<CnaeCsv, Cnae> getProcessor() {
		return new ItemProcessor<CnaeCsv, Cnae>() {
			@Override
			@Nullable
			public Cnae process(@NonNull CnaeCsv csv) throws Exception {

				Cnae cnae = Cnae.builder()
						.id(toInteger(csv.getCodCNAE_col1_a()))
						.description(csv.getDescricao_col2_b())
						.build();

				return cnae;
			}
		};
	}

	@Override
	public Class<CnaeCsv> getCsvClass() {
		return CnaeCsv.class;
	}

	@Override
	public String getFilePattern() {
		return "Cnaes.zip";
	}

	@Override
	public String[] getColumnNames() {
		return new String[] {
				"codCNAE_col1_a",
				"descricao_col2_b",
		};
	}

	@Override
	public int[] getIncludeColumns() {
		return new int[] { 0, 1 };
	}

}
