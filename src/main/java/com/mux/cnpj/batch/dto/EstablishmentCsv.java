package com.mux.cnpj.batch.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Data
public class EstablishmentCsv {
	private String cnpj_colA_1;
	private String filial_colB_2;
	private String digitoVerificador_colC_3;
	private String indMatrizFilialRepetido_colD_4;
	private String nomeFantasia_colE_5;
	private String situacaoCadastral_colF_6;
	private String dataSituacaoCadastral_colG_7;
	private String motivoSituacaoCadastral_colH_8;
	private String nomeCidadeExterior_colI_9;
	private String codPais_colJ_10;
	private String dataInicioAtividade_colK_11;
	private String cnaeFiscalPrincipal_colL_12;
	private String cnaeFiscal_colM_13;
	private String tipoLograodouro_colN_14;
	private String logradouro_colO_15;
	private String numero_colP_16;
	private String complemento_colQ_17;
	private String bairro_colR_18;
	private String cep_colS_19;
	private String uf_colT_20;
	private String codMunicipio_colU_21;
	private String ddd1_colV_22;
	private String tel1_colW_23;
	private String ddd2_colV_24;
	private String tel3_colW_25;
	private String fax_colV_26;
	private String fax_colW_27;
	private String correioEletronico_colAB_28;
}
