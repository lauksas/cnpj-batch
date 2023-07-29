package com.mux.cnpj.batch.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Data
public class SimpleOptantsCsv {

	private String cnpj_col1_A;
	//S ou N
	private String opcaoSimples_col2_B;

	private String dataOpcaoSimples_col3_C;

	private String dataExclusaoSimples_col4_D;
	//S ou N
	private String opcaoMei_col5_E;

	private String dataOpcaoMei_col6_F;

	private String dataExclusaoMei_col7_G;
}
