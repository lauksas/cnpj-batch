package com.mux.cnpj.batch.data.entity;

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
public class LegalNature {
	@Id
	@Column(columnDefinition = "smallint")
	private Integer id;

	@Column(columnDefinition = "text")
	private String description;
}
