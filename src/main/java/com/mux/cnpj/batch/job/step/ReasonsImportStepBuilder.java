package com.mux.cnpj.batch.job.step;

import static com.mux.cnpj.batch.formatter.CsvFormatter.toInteger;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.mux.cnpj.batch.data.entity.Reason;
import com.mux.cnpj.batch.dto.ReasonCsv;
import com.mux.cnpj.batch.job.step.factory.AbstractCNPJStepBuilder;

@Component
public class ReasonsImportStepBuilder extends AbstractCNPJStepBuilder<ReasonCsv, Reason> {

	@Override
	public ItemProcessor<ReasonCsv, Reason> getProcessor() {
		return new ItemProcessor<ReasonCsv, Reason>() {

			@Override
			public Reason process(ReasonCsv item) throws Exception {
				return Reason.builder()
						.id(toInteger(item.getCodMotivoSituacaoCadastral_col1_a()))
						.description(item.getDescricao_col2_b().trim())
						.build();
			}

		};
	}

	@Override
	public Class<ReasonCsv> getCsvClass() {
		return ReasonCsv.class;
	}

	@Override
	public String getFilePattern() {
		return "Motivos.zip";
	}

	@Override
	public String[] getColumnNames() {
		return new String[] {
				"codMotivoSituacaoCadastral_col1_a",
				"descricao_col2_b"
		};
	}

	@Override
	public int[] getIncludeColumns() {
		return new int[] { 0, 1 };
	}

}
