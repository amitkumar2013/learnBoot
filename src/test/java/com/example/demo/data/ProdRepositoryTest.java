package com.example.demo.data;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.example.demo.data.unstruc.Product;
import com.example.demo.data.unstruc.ProductDao;

/*
 * Specific DAO tests
 * @DataJpaTest does some standard setup needed for testing the persistence layer, viz: 
 * configuring H2Db, Hibernate, Spring Data, DataSource, @EntityScan & SQL logging
 */
// Although Validation can be done here with @Valid - it would be rather too late.
@DataJpaTest
@Tag("regression")
public class ProdRepositoryTest {
 
	// Sets up some data - same as EntityManager
    @Autowired
    private TestEntityManager entityManager;
 
    @Autowired
    private ProductDao prodRepo;
 
    @Test
    public void whenFindByName_thenReturnProduct() {
        // setup
        Product testProduct = new Product(null, "product1", 20);
        entityManager.persist(testProduct);
        entityManager.flush();
     
        // test
        long id = prodRepo.count();
        Product found = prodRepo.findById(id).get();
     
        // assertJ instead of junit5's jupiter 
        assertThat(found.getName()).isEqualTo("product1");
    } 
}