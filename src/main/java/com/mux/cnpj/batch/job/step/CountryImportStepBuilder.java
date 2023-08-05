package com.mux.cnpj.batch.job.step;

import static com.mux.cnpj.batch.formatter.CsvFormatter.toInteger;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import com.mux.cnpj.batch.data.entity.Country;
import com.mux.cnpj.batch.dto.CountryCsv;
import com.mux.cnpj.batch.job.step.factory.AbstractCNPJStepBuilder;

@Component
public class CountryImportStepBuilder extends AbstractCNPJStepBuilder<CountryCsv, Country> {

	@Override
	public ItemProcessor<CountryCsv, Country> getProcessor() {
		return new ItemProcessor<CountryCsv, Country>() {
			@Override
			@Nullable
			public Country process(@NonNull CountryCsv csv) throws Exception {

				Country cnae = Country.builder()
						.id(toInteger(csv.getCodPais_col1_a()))
						.name(csv.getNome_col2_b())
						.build();

				return cnae;
			}
		};
	}

	@Override
	public Class<CountryCsv> getCsvClass() {
		return CountryCsv.class;
	}

	@Override
	public String getFilePattern() {
		return "Paises.zip";
	}

	@Override
	public String[] getColumnNames() {
		return new String[] {
				"codPais_col1_a",
				"nome_col2_b",
		};
	}

	@Override
	public int[] getIncludeColumns() {
		return new int[] { 0, 1 };
	}

}
