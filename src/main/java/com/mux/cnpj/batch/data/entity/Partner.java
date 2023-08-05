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
public class Partner {

	@Id
	@Column(columnDefinition = "text")
	private String id;

	private BigDecimal maskedCpfOrCnpj;

	@ManyToOne
	@JoinColumn(foreignKey = @ForeignKey(name = "partner_company_fk"))
	private Company company;

	@ManyToOne
	//@formatter:off
	@JoinColumn(
		columnDefinition = "smallint",
		 foreignKey = @ForeignKey(name = "partner_person_type_fk")
	)
	//@formatter:on
	private PersonType personType;

	@Column(columnDefinition = "text")
	private String name;

	@ManyToOne
	@JoinColumn(foreignKey = @ForeignKey(name = "partner_qualification_fk"))
	private PartnerQualification qualification;

	@ManyToOne
	@JoinColumn(foreignKey = @ForeignKey(name = "partner_country_fk"))
	private Country country;

	private Integer legalRepresentantMaskedCpf;
	private String legalRepresentantName;

	@ManyToOne
	//@formatter:off
	@JoinColumn(
		foreignKey = @ForeignKey(
				name = "partner_legal_representant_qualification_fk"
			)
	)
	//@formatter:on
	private PartnerQualification legalRepresentantQualification;

	public Partner craftId() {

		StringBuilder id = new StringBuilder(50);

		if (maskedCpfOrCnpj != null) {
			id.append(maskedCpfOrCnpj);
		}

		id.append(":")
				.append(company.getCnpj())
				.append(":")
				.append(personType)
				.append(":");

		if (personType.getId().equals(3)) {
			id.append(name.replaceAll(" ", "_"));
		}
		this.id = id.toString();

		return this;
	}

}
