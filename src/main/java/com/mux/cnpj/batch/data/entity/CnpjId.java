package com.mux.cnpj.batch.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Data
@Embeddable
@Table(indexes = { @Index(name = "cnpj_index", columnList = "cnpj", unique = false) })
public class CnpjId {
	@Column(columnDefinition = "integer")
	private Integer cnpj;

	@Column(columnDefinition = "smallint")
	private Integer headquartersPart;

	@Column(columnDefinition = "smallint")
	private Integer checkDigit;
}
