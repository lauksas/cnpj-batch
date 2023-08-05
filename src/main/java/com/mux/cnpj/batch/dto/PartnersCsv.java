package com.mux.cnpj.batch.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Data
public class PartnersCsv {
	private String cnpj_colA_1;
	// 1 pessoa jurídica | 2 pessoa física | 3 estrangeiro
	private String tipoSocio_colB_2;
	private String nomeSocio_colC_3;
	private String CnpjSocioOuCpfMascarado_colD_4;
	private String qualificacao_colE_5;
	private String dataEntrada_colF_6;
	private String codPaisSocioEstrangeiro_colG_7;
	private String cpfMascaradoRepresentanteLegal_colH_8;
	private String nomeRepresentanteLegal_colI_9;
	private String qualificacaoRepresentanteLegal_colJ_10;
	private String colK_11;
}
