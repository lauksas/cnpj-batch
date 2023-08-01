package com.mux.cnpj.batch.data.entity;

import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Data
public class Cnae {
	@Id
	private Integer id;

	@Column(columnDefinition = "text")
	private String description;

	@ManyToMany
	private Set<Estabilishment> estabilishments;
}
