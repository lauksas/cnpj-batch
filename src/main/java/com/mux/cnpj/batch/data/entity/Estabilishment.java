package com.mux.cnpj.batch.data.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Data
@Entity
public class Estabilishment {
	
	@EmbeddedId
	private CnpjId cnpjId;

	@Column(columnDefinition = "smallint")
	private Integer headquartersIndicator;
	
	@Column(columnDefinition = "text")
	private String tradeName;

	@Column(columnDefinition = "smallint")
	private Integer statusId;

	@Column(columnDefinition = "int")
	private Integer mainCnaeFiscal;

	@Column(columnDefinition = "text")
	private String fiscalCenae;

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

	@Column(columnDefinition = "smallint")
	private Integer cityCode;

	@Column(columnDefinition = "smallint")
	private Integer areaCode1;

	@Column(columnDefinition = "int")
	private Integer telephone1;

	@Column(columnDefinition = "smallint")
	private Integer areaCode2;

	@Column(columnDefinition = "int")
	private Integer telephone2;

	@Column(columnDefinition = "smallint")
	private Integer faxAreaCode;

	@Column(columnDefinition = "int")
	private Integer faxTelephone;

	@Column(columnDefinition = "timestamptz")
	private LocalDateTime lastUpdated;
}
