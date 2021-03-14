package com.example.demo;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.json.JSONException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


/*
 * This is an integration test for a whole server at any random port including db and other external endpoints.
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT) // MOCK is default besides RANDOM_PORT, DEFINED_PORT & NONE
@Tag("integration")
public class IntgRandomPortTest {

    private static final ObjectMapper jsonMapper = new ObjectMapper();

	@LocalServerPort // Way to get the reference of the above random port.
	private int port;

	private TestRestTemplate restTemplate = new TestRestTemplate();
	
	// Service layer is available
    @Test
    public void testSvc() 
    {
        String responseObj = this.restTemplate.getForObject("http://localhost:" + port + "/hello/amit", String.class);
        // Junit 5 has not equivalent assertThat rather 
        // contains, hasSize, is, not, sameInstance, notNullValue etc. also a combo like not(sameInstance(...
		assertThat(responseObj, is("Hello AMIT!"));
    }

	//@Sql({ "test-schema.sql", "test-data.sql" }) //Doesn't work
    @Test
    public void testDao() 
    {
    	// For more elaborate call viz. headers and POST 
        HttpEntity<String> entity = new HttpEntity<String>(null, new HttpHeaders());
        ResponseEntity<String> response = restTemplate.exchange("http://localhost:" + port + "/user/id/1", HttpMethod.GET, entity, String.class);
        assertThat(response.getBody(), is("Dangote, Aliko"));
    }
    
	@Disabled
	public void save_emptyAuthor_emptyPrice_400() throws JSONException {
		// Create request load
		String bookInJson = "{\"name\":\"ABC\"}";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<>(bookInJson, headers);

		// send json with POST
		ResponseEntity<String> response = restTemplate.postForEntity("/books", entity, String.class);
		// printJSON(response);

		String expectedJson = "{\"status\":400,\"errors\":[\"Author is not allowed.\",\"Please provide a price\",\"Please provide a author\"]}";
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals(expectedJson, response.getBody(), "Respose failed message");
		// Check if mockRepo was indeed called 1 time a `someMethod` with any String argument
		//verify(mockRepository, times(1)).someMethod(ArgumentMatchers.any(String.class));
	}
    
	private static void printJSON(Object object) {
		String result;
		try {
			result = jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
			System.out.println(result);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

}
