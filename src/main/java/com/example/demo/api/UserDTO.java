package com.example.demo.api;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.example.demo.data.struc.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserDTO {

	@NotNull
	private String crowdId;

	@NotNull
	@Size(min = 1)
	private List<User> users;
}
