package com.example.demo.commons;

import lombok.Getter;

/**
 * Class representing language sensitive constants.
 * 
 * @author amit.30.kumar
 */
@Getter
public enum I18Constants {
	
	NO_ITEM_FOUND("item.absent");

	String key;

	I18Constants(String key) {
		this.key = key;
	}
}
