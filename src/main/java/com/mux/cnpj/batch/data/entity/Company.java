package com.mux.cnpj.batch.data.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

	@ManyToOne
	//@formatter:off
	@JoinColumn(
		columnDefinition = "smallint",
		 foreignKey = @ForeignKey(name = "company_legal_nature_fk")
	)
	//@formatter:on
	private LegalNature legalNature;

	@ManyToOne
	//@formatter:off
	@JoinColumn(
		columnDefinition = "smallint",
		 foreignKey = @ForeignKey(name = "company_reason_fk")
	)
	//@formatter:off
	private Reason closeDownReason;

	@Column(columnDefinition = "bigint")
	private BigDecimal socialCapital;

	//0 ou vazio: não informado
	//1 micro empresa
	//3 empresa pequeno porte
	//5 demais
	@Column(columnDefinition = "smallint")
	private Integer companySize;
}
