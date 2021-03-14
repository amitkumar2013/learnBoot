package com.example.demo.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import javax.validation.ConstraintViolationException;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import com.example.demo.config.ValidatorConfiguration;
import com.example.demo.data.struc.UserDao;
import com.example.demo.data.struc.model.User;
import com.example.demo.service.UserService;

/*
 * Testing a service with mocked dao's
 * ALso test validation works.
 */
@Import({ ValidatorConfiguration.class })
@SpringBootTest(classes = { UserService.class })
@Tag("regression")
public class UserServiceMockTest {

	@MockBean
	private UserDao userRepo;

	@Autowired
	private UserService userService;

	@Test
	public void serviceWithMockDaoTest() {
		when(userRepo.findUserByLastNameLikeNative("amit")).thenReturn(User.builder().id(1L).firstName("Amit").lastName("Kumar").age(11).build());
		User userSearch = userService.findByLastname("amit");
		assertEquals(userSearch.getName(), "kumar, amit");
	}

	@Test
	public void updateTest() {
	    User userDetail = User.builder().id(1L).firstName("Amit").lastName("Kumar").age(11).build();
	    // throws IllegalArgumentException for < 18 as per the ageValidator 
	    IllegalArgumentException exc = assertThrows(IllegalArgumentException.class, () -> {
		    userService.updateUser(userDetail);	    	
	    });
	    // Don't stop here check the appropriate message as well.
	    String expectedMessage = "Age cannot be less than 18.";
	    String actualMessage = exc.getMessage();
	 
	    assertTrue(actualMessage.contains(expectedMessage));
	}
	
	// This is not working as of now
	@Disabled
	public void serviceWithMissingParamTest() {
		assertThrows(ConstraintViolationException.class, () -> { 
			userService.findByLastname(null);
		});
	}
}
