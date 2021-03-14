package com.example.demo.data.graphql.model;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "vehicles")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("serial")
public class Vehicle implements Serializable {

	@Id
	@Column(name = "id", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "type", nullable = false)
	private String type;

	@Column(name = "model_code", nullable = false)
	private String modelCode;

	@Column(name = "brand_name")
	private String brandName;

	@Column(name = "launch_date")
	private LocalDate launchDate;

	private transient String formattedDate;

	// Getter and setter
	public String getFormattedDate() {
		LocalDate date = getLaunchDate();
		return date != null ? date.toString() : "";
	}
}