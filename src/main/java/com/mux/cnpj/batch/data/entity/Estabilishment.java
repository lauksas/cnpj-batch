package com.mux.cnpj.batch.data.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Data
@Entity
//@formatter:off
@Table(
	indexes = {
		 @Index(name = "cnpj_index", columnList = "cnpj", unique = false) 
	}
)
//@formatter:on
public class Estabilishment {

	@EmbeddedId
	private CnpjId cnpjId;

	@Column(columnDefinition = "int2")
	private Integer headquartersIndicator;

	@Column(columnDefinition = "text")
	private String tradeName;

	@Column(columnDefinition = "int2")
	private Integer statusId;

	@ManyToOne
	//@formatter:off
	@JoinColumn(
		columnDefinition = "int",
		 foreignKey = @ForeignKey(name = "estabilishment_main_cnae_fk")
	)
	//@formatter:on
	private Cnae mainCnaeFiscal;

	@ManyToMany
	//@formatter:off
	@JoinTable(
		inverseForeignKey = @ForeignKey(name = "cnae_estabilishment_fk"),
		foreignKey = @ForeignKey(name = "estabilishment_cnae_composite_fk")
	)
	//@formatter:on
	private Set<Cnae> fiscalCenae;

	@Column(columnDefinition = "text")
	private String streetType;

	@Column(columnDefinition = "text")
	private String street;

	@Column(columnDefinition = "text")
	private String streetNumber;

	@Column(columnDefinition = "text")
	private String complement;

	@Column(columnDefinition = "text")
	private String district;

	@Column(columnDefinition = "int")
	private Integer zipCode;

	@Column(columnDefinition = "text")
	private String stateCode;

	@Column(columnDefinition = "date")
	private LocalDate created;

	@ManyToOne
	//@formatter:off
	@JoinColumn(
		columnDefinition = "int2",
		foreignKey = @ForeignKey(name = "estabilishment_municipality_fk")
	)
	//@formatter:on
	private Municipality cityCode;

	@Column(columnDefinition = "int2")
	private Integer areaCode1;

	@Column(columnDefinition = "int")
	private Integer telephone1;

	@Column(columnDefinition = "int2")
	private Integer areaCode2;

	@Column(columnDefinition = "int")
	private Integer telephone2;

	@Column(columnDefinition = "int2")
	private Integer faxAreaCode;

	@Column(columnDefinition = "int")
	private Integer faxTelephone;

	@Column(columnDefinition = "timestamptz")
	private LocalDateTime lastUpdated;

	@Column(columnDefinition = "text")
	private String email;

	@ManyToOne
	// @formatter:off
	@JoinColumn(
		name = "cnpj",
		referencedColumnName = "cnpj",
		insertable = false,
		updatable = false,
		foreignKey = @ForeignKey(name = "establishment_company_fk")
		)
	// @formatter:on
	private Company company;
}
