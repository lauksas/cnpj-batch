package com.mux.cnpj.batch.job.step;

import static com.mux.cnpj.batch.formatter.CsvFormatter.toInteger;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.mux.cnpj.batch.data.entity.Municipality;
import com.mux.cnpj.batch.dto.MunicipalityCsv;
import com.mux.cnpj.batch.job.step.factory.AbstractCNPJStepBuilder;

@Component
public class MunicipalityImportStepBuilder extends AbstractCNPJStepBuilder<MunicipalityCsv, Municipality> {

	@Override
	public ItemProcessor<MunicipalityCsv, Municipality> getProcessor() {
		return new ItemProcessor<MunicipalityCsv, Municipality>() {

			@Override
			public Municipality process(MunicipalityCsv item) throws Exception {
				return Municipality.builder()
						.id(toInteger(item.getCodMunicipio_col1_a()))
						.name(item.getNome_col2_b().trim())
						.build();
			}

		};
	}

	@Override
	public Class<MunicipalityCsv> getCsvClass() {
		return MunicipalityCsv.class;
	}

	@Override
	public String getFilePattern() {
		return "Municipios.zip";
	}

	@Override
	public String[] getColumnNames() {
		return new String[] {
				"codMunicipio_col1_a",
				"nome_col2_b"
		};
	}

	@Override
	public int[] getIncludeColumns() {
		return new int[] { 0, 1 };
	}

}
