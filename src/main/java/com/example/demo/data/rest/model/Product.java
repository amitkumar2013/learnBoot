package com.example.demo.data.rest.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Min;

//import org.hibernate.search.annotations.*;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

// JPA
@Entity @Table(name = "products")
// Util
@Builder @AllArgsConstructor @NoArgsConstructor @Slf4j @SuppressWarnings("serial") @EqualsAndHashCode
//This will use hibernate search
//@Indexed 
@Data
//@ToString(exclude = "id")
public class Product implements Serializable {

	@Id
	@Setter(AccessLevel.PROTECTED)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// index=Index.YES, analyze=Analyze.YES and store=Store.NO are the default values
	// Store enables projection
	// @Field(index = Index.YES, analyze=Analyze.NO, store = Store.YES) 
    // New Style - (non-analyzed):  @GenericField
	private @NonNull String name;

    // New Style - (analyzed):  @FullTextField
    //@Field private String description;
    //@Field 
    private @Min(0) double price;

	public String getName() {
		log.debug("lombok does provide a log handles");
		return this.name;
	}
	
	// @IndexedEmbedded - TODO
	// @DateBridge(resolution=Resolution.DAY) - TODO
}
