package com.example.demo.api;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.example.demo.data.struc.model.User;

import lombok.Data;

/**
 * Sample data transfer with Validation built in.
 * 
 * @author amit.30.kumar
 */
@Data
public class UserDTO {

	@NotNull
	private String crowdId;

	@NotNull
	@Size(min = 1)
	private List<User> users;
}
