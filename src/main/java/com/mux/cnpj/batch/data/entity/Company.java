package com.mux.cnpj.batch.data.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Data
public class Company {
	
	@Id
	@Column(columnDefinition = "int")
	private Integer cnpj;
	
	@Column(columnDefinition = "text")
	private String name;

	@Column(columnDefinition = "smallint")
	private Integer legalNature;

	@Column(columnDefinition = "smallint")
	private Integer closeDownReason;

	@Column(columnDefinition = "bigint")
	private BigDecimal socialCapital;

	//0 ou vazio: não informado
	//1 micro empresa
	//3 empresa pequeno porte
	//5 demais
	@Column(columnDefinition = "smallint")
	private Integer companySize;
}
