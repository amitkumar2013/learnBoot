package com.example.demo.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import com.example.demo.service.VendorAdaptor;

//import com.fasterxml.jackson.databind.ObjectMapper;

/*
 * Specific API mocking tests
 * @RestClientTest Mocks vendor - only works if code being used is rest template
 */
@RestClientTest(VendorAdaptor.class)
@Tag("regression")
public class VendorAdaptorTest {

	@Autowired
	private MockRestServiceServer server;
	
	@Autowired
	private VendorAdaptor client;

	//@Autowired
    //private ObjectMapper objectMapper;	
    //String userDetail = objectMapper.writeValueAsString("<user></user>");
	//this.server.expect(requestTo("/user/1")).andExpect(method(HttpMethod.GET))
    //  .andRespond(withSuccess(userDetail, MediaType.APPLICATION_JSON));
    
	@Test
	public void testServiceCall() {
		this.server.expect(requestTo("http://othervendor/users"))
			.andExpect(method(HttpMethod.GET))
			.andRespond(withSuccess("<users></users>", MediaType.TEXT_PLAIN));

		String userServiceResponse = client.testVendorAPI();
		assertThat(userServiceResponse).isEqualTo("<users></users>");
	}

}
