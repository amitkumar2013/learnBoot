package com.example.demo.data.search.solr;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSolrRepo extends SolrCrudRepository<UserDoc, String> {
	@Query(value = "*:*")
	List<UserDoc> getUsers();
	
    @Query("id:*?0* OR name:*?0*")
    public Page<UserDoc> findByCustomQuery(String searchTerm, Pageable pageable);
 
    // the following needs a 'solr-named-queries.properties' file with content
    // Product.findByNamedQuery=id:*?0* OR name:*?0*
    @Query(name = "Product.findByNamedQuery")
    public Page<UserDoc> findByNamedQuery(String searchTerm, Pageable pageable);
}
