package com.example.demo.api;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.data.struc.model.User;
import com.example.demo.service.MiscService;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(UserController.class)
@Tag("regression")
// Not Shown: In case we need specific configuration like testdb or autowire does not work: 
//@Import(MyTestConfiguration.class)
public class UserCtrlMockTest {

	@Autowired
	private MockMvc mockMvc;

	// ************ GET ************

	// This would also be needed
	@MockBean
	private MiscService greetingService;
	@Test
	public void tryMvc() throws Exception {
		// A mock greeting service
		when(greetingService.greet("amit")).thenReturn("Hello amit!");
		this.mockMvc.perform(get("/hello/amit"))
					//.andDo(print())
					.andExpect(status().isOk())
					.andExpect(content().string(containsString("Hello amit!")));
	}

	// Since the service layer is mocked: other layers like dao need not be mocked
	@MockBean
	private UserService userService;
	@Test
	public void tryMockMvc() throws Exception {
		// A mock UserService
		when(userService.findByLastname("amit")).thenReturn(User.builder().id(1L).firstName("Amit").lastName("Kumar").age(11).build());
		this.mockMvc.perform(get("/user/name/amit"))
					//.andDo(print())
					.andExpect(status().isOk())
					.andExpect(content().string(containsString("kumar, amit")));
	}

	// ************ POST ************
	// ************ +ve *************
	@Autowired
    private ObjectMapper objectMapper;	
	@Test
	public void tryPost() throws Exception {
		// create json
	    String userDetail = objectMapper.writeValueAsString(User.builder().id(1L).firstName("Amit").lastName("Kumar").age(11).build());
		this.mockMvc.perform(post("/updateUser").content(userDetail)
	      			.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
					//.andDo(print())
					.andExpect(status().isOk());
	}

	// ************ -ve *************
	@Test
	public void tryPost_thenFailValidationOnPayload() throws Exception {
		// Not meeting the parameter's attribute and failing Validation
	    String userDetail = objectMapper.writeValueAsString(User.builder().id(1L).firstName("Amit").lastName("Kumar").age(11).build());
		this.mockMvc.perform(post("/updateUser").content(userDetail)
	      			.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
					.andDo(print())
					.andExpect(status().isBadRequest());
	}

	@Test
	public void whenPathVariableIsInvalid_thenReturnsStatus400() throws Exception {
		// Not passing an integer digit
		this.mockMvc.perform(get("/user/id/test"))
					.andExpect(status().isBadRequest());
	}	
	
	@Test
	public void whenRequestParameterIsInvalid_thenReturnsStatus400() throws Exception {
		// Not passing parameter in limit i.e. <100
		this.mockMvc.perform(get("/user")
					.param("id", "123"))
					.andExpect(status().isBadRequest())
					.andExpect(content().string(containsString("not valid")));
	}
}
