package com.mux.cnpj.batch.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Data
public class LegalNatureCsv {
	private String codNaturezaLegal_col1_a;
	private String descricao_col2_b;
}
