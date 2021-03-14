package com.example.demo.data.struc;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.data.struc.model.User;

/**
 * Sample Spring data JPA Repository - backed by H2
 * Avoids templates (JpaTemplate, HibernateTemplate)
 * 
 * @author amit.30.kumar
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
	@Query("SELECT u FROM User u WHERE LOWER(u.lastName) = LOWER(:lastName)")
	User retrieveByLastName(@Param("lastName") String lastName);

	// Native SQL
	@Query(value = "SELECT * FROM users u WHERE u.lastName LIKE ?1%", nativeQuery = true)
	User findUserByLastNameLikeNative(String lastName);

	// Combo
	@Query(value = "SELECT * FROM Users ORDER BY id", countQuery = "SELECT count(*) FROM Users", nativeQuery = true)
	Page<User> findAllUsersWithPaginationNative(Pageable pageable);

	// Insert, Update or Delete
	@Modifying
	@Query(value = "INSERT INTO Users (lastName, age) VALUES (:lastName, :age)", nativeQuery = true)
	void insertUser(@Param("lastName") String lastName, @Param("age") Integer age);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("delete User u where u.age > 100")
	int deleteDeactivatedUsers();
	
	//@Procedure(name = "count_by_name")
	//long getCountByName(@Param("name") String name);
}
