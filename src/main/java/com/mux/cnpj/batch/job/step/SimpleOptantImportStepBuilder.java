package com.mux.cnpj.batch.job.step;

import static com.mux.cnpj.batch.formatter.CsvFormatter.fromString;
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

import com.mux.cnpj.batch.data.entity.SimpleOptant;
import com.mux.cnpj.batch.dto.SimpleOptantsCsv;
import com.mux.cnpj.batch.job.step.factory.AbstractCNPJStepBuilder;
import com.mux.cnpj.batch.job.writer.JdbcBatchItemWriterCNPJ;
import com.mux.cnpj.config.DataSourceConfig;

@Component
public class SimpleOptantImportStepBuilder extends AbstractCNPJStepBuilder<SimpleOptantsCsv, SimpleOptant> {

	private static final String query = """
					INSERT INTO cnpj.simple_optant
						(cnpj, simple_optant, mei_optant)
						select :cnpj, :simpleOptant, :meiOptant
						where exists
							(select 1 from cnpj.establishment es where es.cnpj=:cnpj limit 1)
					on conflict (cnpj)
						do update set
						simple_optant = :simpleOptant,
						mei_optant = :meiOptant
					;
			""";

	@Override
	public ItemProcessor<SimpleOptantsCsv, SimpleOptant> getProcessor() {
		return new ItemProcessor<SimpleOptantsCsv, SimpleOptant>() {
			@Override
			@Nullable
			public SimpleOptant process(@NonNull SimpleOptantsCsv csv) throws Exception {

				SimpleOptant company = SimpleOptant.builder()
						.cnpj(toInteger(csv.getCnpj_col1_A()))
						.simpleOptant(fromString(csv.getOpcaoSimples_col2_B()))
						.meiOptant(fromString(csv.getOpcaoMei_col5_E()))
						.build();

				return company;
			}
		};
	}

	@Override
	public Class<SimpleOptantsCsv> getCsvClass() {
		return SimpleOptantsCsv.class;
	}

	@Override
	public String getFilePattern() {
		return "Simples*.zip";
	}

	@Override
	public String[] getColumnNames() {
		return new String[] {
				"cnpj_col1_A",
				"opcaoSimples_col2_B",
				"opcaoMei_col5_E",
		};
	}

	@Override
	public int[] getIncludeColumns() {
		return new int[] { 0, 1, 4 };
	}

	@Autowired
	@Qualifier(DataSourceConfig.CNPJ_DATASOURCE_BEAN_NAME)
	DataSource cnpjDataSource;

	@Override
	public ItemWriter<SimpleOptant> getWriter() {
		JdbcBatchItemWriterCNPJ<SimpleOptant> writer = new JdbcBatchItemWriterCNPJ<>();
		writer.setDataSource(cnpjDataSource);
		writer.setAssertUpdates(false);
		writer.setSql(query);
		writer.setUsingNamedParameters(true);
		writer.setItemSqlParameterSourceProvider(new ItemSqlParameterSourceProvider<SimpleOptant>() {

			@Override
			public SqlParameterSource createSqlParameterSource(SimpleOptant item) {
				Map<String, Object> map = new HashMap<>();

				map.put("cnpj", item.getCnpj());
				map.put("simpleOptant", item.getSimpleOptant());
				map.put("meiOptant", item.getMeiOptant());

				return new MapSqlParameterSource(map);
			}

		});

		return writer;
	}

}
