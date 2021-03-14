package com.example.demo.data.struc;

import javax.persistence.NamedStoredProcedureQueries;
import javax.persistence.NamedStoredProcedureQuery;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureParameter;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

/*
 * Sample Spring data JPA Repository - backed by H2
 * Avoids templates (JpaTemplate, HibernateTemplate)
 */
/*
@NamedStoredProcedureQueries({
		@NamedStoredProcedureQuery(name = "count_by_name", procedureName = "person.count_by_name", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "name", type = String.class),
				@StoredProcedureParameter(mode = ParameterMode.OUT, name = "count", type = Long.class) }) })
*/
public interface UserDao extends JpaRepository<User, Long> {

	// Besides Default methods these are also supported
	
	// JPA Query
	@Query("SELECT u FROM User u WHERE LOWER(u.lastname) = LOWER(:lastname)")
	User retrieveByLastname(@Param("lastname") String lastName);

	// Native SQL
	@Query(value = "SELECT * FROM users u WHERE u.lastname LIKE ?1%", nativeQuery = true)
	User findUserByLastnameLikeNative(String lastname);

	// Combo
	@Query(value = "SELECT * FROM Users ORDER BY id", countQuery = "SELECT count(*) FROM Users", nativeQuery = true)
	Page<User> findAllUsersWithPaginationNative(Pageable pageable);

	// Insert, Update or Delete
	@Modifying
	@Query(value = "INSERT INTO Users (lastname, age) VALUES (:lastname, :age)", nativeQuery = true)
	void insertUser(@Param("lastname") String lastname, @Param("age") Integer age);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("delete User u where u.age > 100")
	int deleteDeactivatedUsers();
	
	//@Procedure(name = "count_by_name")
	//long getCountByName(@Param("name") String name);
}
