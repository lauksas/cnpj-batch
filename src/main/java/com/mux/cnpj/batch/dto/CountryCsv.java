package com.mux.cnpj.batch.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Data
public class CountryCsv {
	private String codPais_col1_a;
	private String nome_col2_b;
}
