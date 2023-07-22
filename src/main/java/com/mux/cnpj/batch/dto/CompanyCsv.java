package com.mux.cnpj.batch.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Data
public class CompanyCsv {
	
	private String cnpj_col1_A;
	
	private String nome_col2_B;

	private String codNaturezaJuridica_col3_C;

	private String motivoSituacaoCadastral_col4_D;

	private String socialCapital_col5_E;

	//0 ou vazio: não informado
	//1 micro empresa
	//3 empresa pequeno porte
	//5 demais
	private String codPorteEmpresa_col6_F;
	
	private String estadoEUnidadeFederativa_col7_G;
}
