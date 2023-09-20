package com.mux.cnpj.batch.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Data
@Embeddable
public class CnpjId {
	@Column(columnDefinition = "integer")
	private Integer cnpj;

	@Column(columnDefinition = "int2")
	private Integer headquartersPart;

	@Column(columnDefinition = "int2")
	private Integer checkDigit;
}
