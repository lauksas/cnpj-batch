package com.mux.cnpj.batch.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Data
public class SimpleOptant {

	@Id
	@Column(columnDefinition = "int")
	private Integer cnpj;

	@Column(nullable = true)
	private Boolean simpleOptant;

	@Column(nullable = true)
	private Boolean meiOptant;

	@OneToOne
	@MapsId
	@JoinColumn(name = "cnpj", foreignKey = @ForeignKey(name = "simple_optant_company_fk"))
	private Company company;

}
