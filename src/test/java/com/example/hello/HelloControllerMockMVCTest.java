package hello;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import hello.api.HelloService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes= {hello.Application.class, hello.api.HelloController.class})
//Similar to Doesn't work though 
//@WebMvcTest({hello.api.HelloController.class,hello.Application.class}) // Don't create the whole application rather just the web layers
public class HelloControllerMockMVCTest {

	// Can send a request and test it.
	//@Autowired 
	private MockMvc mvc;
	// Stopped working with module levevl scopes. Hence the following
	@Autowired
	private WebApplicationContext webApplicationContext;
	@Before
	public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
		mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	// This bean is required as its a part of dependency of HelloController
	// providing service for /; but so far it does not do anything which may be
	// accommodated with a setup as shown below.
	@MockBean
	private HelloService helloService;

	// @Before
	// public void setUp() throws Exception {
	// given(this.helloService.getXXX(id)).willReturn(new XXX(arg));
	// }

	
	@Test
	public void getHello() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().string(equalTo("Greetings from Spring Boot!")));
	}
}