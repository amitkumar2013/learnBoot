package com.example.demo;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.data.struc.User;
import com.example.demo.data.struc.UserDao;
import com.example.demo.data.unstruc.ProductDao;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/*
 * Integration tests: application shall run in ApplicationContext and run tests in it. 
 * Though will not connect to actual network or db. This is different than the complementary IntgRandomPortTest.
 * 
 * STYLE: We save the cost of starting a server -- just mock it: still the whole context is loaded
 * so Spring handles the incoming HTTP request and hands it off to the controller using MockMvc along with auto-configure-mock-mvc.
 */
@SpringBootTest // starts the embedded server, creates web env/app-ctx enables @Test for intg tests
@AutoConfigureMockMvc
//@TestConfiguration // Optional - recommended else its @Configuration
//@ActiveProfiles("test")// Optional to use application-<profile>.yml
@Tag("integration")
public class IntgMockTest {

    private static final ObjectMapper jsonMapper = new ObjectMapper();
	@Autowired
	private MockMvc mockMvc;
		
	// Mocking DB layer in @SpringBootTest will override it
	@MockBean // similar to @Mock just that it registers with Spring context.
    private UserDao userRepo;
    @MockBean
    private ProductDao prodMockRepo;
    
	@Test
	public void tryMockMvc() throws Exception {
		this.mockMvc.perform(get("/")).andExpect(status().is2xxSuccessful());
	}
			
	@Test
	public void tryAnotherMockMvc() throws Exception {
		this.mockMvc.perform(get("/hello/amit"))
				.andDo(print())
				.andExpect(status().isOk()) 
				.andExpect(content().string(containsString("Hello AMIT!")));
	}

	@Test
	public void tryRepoMockMvc() throws Exception{
        when(userRepo.findById(99L)).thenReturn(Optional.of(new User(99L, "Dummy", "Name", 99)));
        // OR another syntax
        //given(userRepo.findById(99L)).willReturn(Optional.of(new User(99L, "Dummy", "Name", 99)));
        this.mockMvc.perform(get("/user/id/99"))
        		.andDo(print())
        		.andExpect(status().isOk())
        		.andExpect(content().string(containsString("Name, Dummy")));
		// It might be prudent to verify if the repo was indeed called once for method findById() 99L as parameter.
        verify(userRepo, times(1)).findById(99L);
	}
	
	@Disabled
	void featureWorksThroughAllLayers() throws Exception {
		/*
		Payload payload = new Payload(param);
		
		mockMvc.perform(post("/uri1/{id}/usecase", 1234)
		        .contentType("application/json")
		        .param("key", "value")
		        .content(objectMapper.writeValueAsString(payload)))
		        .andExpect(status().isOk());
		
		User userSearch = userRepo.retrieveByLastName(param);
		assertThat(userSearch.getName()).isEqualTo(param);
		*/
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
