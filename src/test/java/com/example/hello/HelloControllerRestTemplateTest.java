package com.example.hello;

//import static org.junit.Assert.assertThat;
//import static org.hamcrest.Matchers.equalTo;
// AssertJ is better than Junit
import static org.assertj.core.api.Assertions.assertThat;

import java.net.URL;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

import com.example.demo.api.UserController;
import com.example.demo.service.MiscService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes= {UserController.class})
public class HelloControllerRestTemplateTest {

	// The above starts the dummy server at RANDOM_PORT and this one discovers it.
	@LocalServerPort
	private int port;

	private URL base;

	// Resets across tests - so no @DirtiesContext
	// Similarly @SpyBean
	// @MockBean
	// private XYZService xyzService;
	@MockBean
	private MiscService helloService;

	@Autowired
	private TestRestTemplate template;

	@BeforeEach
	public void setUp() throws Exception {
		this.base = new URL("http://localhost:" + port + "/");
		// given(this.xyzService.getXXX(id)).willReturn(new XXX(arg));
	}

	@Test
	public void getHello() throws Exception {
		ResponseEntity<String> response = template.getForEntity(base.toString(), String.class);
		// JUNIT & HANCREST
		// assertThat(response.getBody(), equalTo("Greet...ot!"));
		// ASSERTJ
		assertThat(response.getBody()).isEqualTo("Greetings from Spring Boot!");
	}
}