package hello.api.person;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
//To avoid in process DB clash
@AutoConfigureTestDatabase(replace = Replace.NONE) 
public class PersonRepositoryTest {

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private PersonRepository repository;

	@Test
	public void findByUsernameShouldReturnUser() {
		this.entityManager.persist(createPerson());
		assertThat(this.repository.findById(4l).get().getFirstName()).isEqualTo("Amit");
	}

	private Person createPerson() {
		Person person = new Person();
		person.setFirstName("Amit");
		person.setLastName("Kumar");
		person.setAge("11");
		return person;
	}

}
