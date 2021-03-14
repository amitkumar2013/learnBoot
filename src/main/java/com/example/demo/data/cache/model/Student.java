package com.example.demo.data.cache.model;

import java.io.Serializable;

import org.springframework.data.redis.core.RedisHash;

import lombok.Builder;
import lombok.Data;

/**
 * The class represents a data structure to be saved with redis.
 * 
 * @author amit.30.kumar
 */
@RedisHash("Student")
@Data
@Builder
@SuppressWarnings("serial")
public class Student implements Serializable {

	public enum Gender {
		MALE, FEMALE
	}

	private String id;
	private String name;
	private Gender gender;
	private int grade;
}