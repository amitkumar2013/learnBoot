package com.example.demo.data;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.example.demo.data.struc.User;
import com.example.demo.data.struc.UserDao;

/*
 * Specific DAO tests
 * @DataJpaTest does some standard setup needed for testing the persistence layer, viz: 
 * configuring H2Db, Hibernate, Spring Data, DataSource, @EntityScan & SQL logging
 */
// Although Validation can be done here with @Valid - it would be rather too late.
@DataJpaTest
@Tag("regression")
public class UserRepositoryTest {
 
	// Sets up some data - same as EntityManager
    @Autowired
    private TestEntityManager entityManager;
 
    @Autowired
    private UserDao userRepo;
 
    @Test
    public void whenFindByName_thenReturnEmployee() {
        // setup
        User testUser = new User(null, "fName", "lName", 20);
        entityManager.persist(testUser);
        entityManager.flush();
     
        // test
        long id = userRepo.count();
        User found = userRepo.findById(id).get();
     
        // assertJ instead of junit5's jupiter 
        assertThat(found.getName()).isEqualTo("lName, fName");
    } 
}