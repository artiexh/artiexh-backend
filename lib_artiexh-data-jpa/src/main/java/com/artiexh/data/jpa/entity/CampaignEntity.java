package com.artiexh.data.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "campaign")
public class CampaignEntity {

	@Id
	@Column(name = "id", nullable = false)
	private Long id;

	@NotNull
	@Column(name = "status", nullable = false)
	private Byte status;

}