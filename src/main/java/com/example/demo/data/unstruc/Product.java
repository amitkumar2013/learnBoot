package com.example.demo.data.unstruc;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Min;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Entity
@Table(name = "products")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(of = { "name" })
@Builder
@Slf4j
@SuppressWarnings("serial")
public class Product implements Serializable {

	@Id
	@Setter(AccessLevel.PROTECTED)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private @NonNull String name;

	private @Min(0) double price;

	public String getName() {
		log.debug("lombok does provide a log handles");
		return this.name;
	}
}
