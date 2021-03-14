package com.example.demo;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.stereotype.Component;

import com.example.demo.data.cache.model.Student;
import com.example.demo.data.cache.model.StudentRepository;
import com.example.demo.data.graphql.VehicleRepository;
import com.example.demo.data.graphql.model.Vehicle;
import com.example.demo.data.nosql.Customer;
import com.example.demo.data.nosql.CustomerDao;
import com.example.demo.data.rest.ProductDao;
import com.example.demo.data.rest.ProductRepo;
import com.example.demo.data.rest.model.Product;
import com.example.demo.data.search.DataIndexer;
import com.example.demo.data.search.solr.UserDoc;
import com.example.demo.data.search.solr.UserSolrRepo;
import com.example.demo.data.struc.UserDao;
import com.example.demo.data.struc.model.User;
import com.example.demo.service.MiscService;

import lombok.extern.slf4j.Slf4j;

/**
 * Sample for invoking some commands after application starts but before it
 * starts serving. viz. populating the database, creating search index etc.
 * 
 * @author amit.30.kumar
 */
@Component
@Slf4j
public class CmdRunner implements CommandLineRunner {

	@Override
	public void run(String... args) throws Exception {
		/*

		// Spring Checks
		listBeans();

		// DB Checks
		testUserRepo(); 	// H2*/
		testVehicleRepo(); 	// GraphQl
/*		testProductRepo();	// MySQL
		testCustomerRepo(); // Mongo
		testStudentRepo(); 	// Redis
		testCache();		// Redis

		// Search Checks
		ingestSolrData();
		testUserSolrRepo();
		testCustomSolrCoreRepo();
*/	
	}

	@Autowired
	private VehicleRepository vehicleRepo;

	private void testVehicleRepo() {
		// vehicleRepo.deleteAll();
		Stream.of("Sedan Civic Honda 2020-01-01", "SUV Breeza Maruti 1900-12-01").forEach(car -> {
			String[] carAttr = car.split(" ");
			LocalDate lDate = LocalDate.parse(carAttr[3]);
			vehicleRepo.save(Vehicle.builder().type(carAttr[0]).modelCode(carAttr[1]).brandName(carAttr[2]).launchDate(lDate).build());
		});
		vehicleRepo.findAll().forEach(System.out::println);
	}

	@Autowired
	private MiscService cacheSvc;
	@Autowired
	private ProductRepo productRepository;
	
	private void testCache() {
		productRepository.deleteAll();
		
		Stream.of("pen", "paper", "pencil").forEach(prodName -> {
			Product prod = Product.builder().id(null).name(prodName).price((50 * Math.random() + 100)).build();
			productRepository.save(prod);
			log.info("Saved product {}", prod);
		});
		
		List<Product> prodList = productRepository.findAll();
		prodList.forEach(System.out::println);
		log.info("All set in DB.");
		prodList.forEach(product -> {
			log.info("Product " + product + " fetching from cache! First fetch puts in cache.");
			log.info(cacheSvc.getProduct(product.getId()).get().toString());
			log.info(cacheSvc.getProduct(product.getId()).get().toString());

			product.setName(product.getName() + "-updated");
			Product updatedProd = cacheSvc.updateProduct(product);
			log.info("Updated Product -> " + updatedProd + " fetching from cache!");
			log.info(cacheSvc.getProduct(product.getId()).get().toString());

			try {
				cacheSvc.deleteProductByID(product.getId());
				log.info(cacheSvc.getProduct(product.getId()).get().toString());
			} catch (Exception exc) {
				log.info("Cache Delete works for " + product.getId());
			}
		});
		log.info("Redis rocks!");
	}

	/*
	 * Solr API search & update-then-search using SpringBoot-JPA
	 */
	@Autowired
	private UserSolrRepo userSolrRepo;
	@Autowired
	private SolrTemplate solrTemplate;
	@Autowired
	private SolrTemplate core1Template;

	private void testCustomSolrCoreRepo() throws SolrServerException, IOException {
		// EITHER
		log.info("Search using generic solr template and specify core as argument and query!");
		List<UserDoc> userDoc1 = solrTemplate.queryForPage("my_core",
				new SimpleQuery(new Criteria(Criteria.WILDCARD).expression(Criteria.WILDCARD)), UserDoc.class)
				.getContent();
		userDoc1.forEach(System.out::println);

		// OR
		log.info("Update using use specific solr template and query!");
		// First Solr update a generic doc
		SolrInputDocument newDoc = new SolrInputDocument();
		newDoc.addField("addr", "India");
		newDoc.setField("email", "new-test@gmail.com");
		// Faceting refers to the classification of the search results into various
		// categories viz. search all authors(specific fields)
		UpdateRequest request = new UpdateRequest();
		// for search: QueryRequest request = new
		// QueryRequest(SolrQuery.setQuery(...).addField(...).addFacetField(...))
		request.setAction(UpdateRequest.ACTION.COMMIT, false, false);
		request.add(newDoc);
		UpdateResponse rsp = request.process(core1Template.getSolrClient());// Using specific template in argument
		userSolrRepo.getUsers().forEach(System.out::println);

		log.info("Response " + rsp.getQTime() + " " + rsp.getElapsedTime()); // QTime & Elapsed time for SLA
	}

	/*
	 * Unstructured Data in Solr repository.
	 */
	private void testUserSolrRepo() {
		log.info("Save using solr and query!");
		// Solr search
		UserDoc userDoc = new UserDoc();
		userDoc.setId("111");
		userDoc.setUsername("Test");
		userDoc.setEmail("test@gmail.com");
		userDoc.setPhoneNumber("1112223334");
		userSolrRepo.save(userDoc); // Direct save/add to http://localhost:8983/solr/<from @SolrDocument - my_core>
		userSolrRepo.getUsers().forEach(System.out::println);
	}

	/*
	 * Take a set of files and index them to Lucene through Solr & use REST /search to test them out
	 */
	@Autowired
	private DataIndexer indexer;
	private void ingestSolrData() throws IOException, CorruptIndexException {
		boolean recreate = true;
		indexer.init(recreate); // Clean previous index utility method
		int indexCount = indexer.createIndex(); // Read and index all '*.txt' files
		indexer.close(); // Don't forget to close
		log.debug("All data has been indexed for search " + indexCount);
	}

	@Autowired
	private CustomerDao custRepository;

	/*
	 * Unstructured Data in Mongo database.
	 */
	private void testCustomerRepo() {
		// custRepository.deleteAll();
		Stream.of("Alice Smith", "Bob Smith").forEach(name -> {
			String[] custName = name.split(" ");
			custRepository.save(Customer.builder().firstName(custName[0]).lastName(custName[1]).build());
		});
		custRepository.findAll().forEach(System.out::println);
	}

	@Autowired
	private ProductDao prodRepository;

	/*
	 * Structured Data in MySql database.
	 */
	private void testProductRepo() {
		// prodRepository.deleteAll();
		Stream.of("pen", "paper", "pencil").forEach(prodName -> {
			Product prod = Product.builder().id(null).name(prodName).price((50 * Math.random() + 100)).build();
			prodRepository.save(prod);
		});
		prodRepository.findAll().forEach(System.out::println);
	}

	@Autowired
	private UserDao userRepository;

	/*
	 * Structured Data in H2 database.
	 */
	private void testUserRepo() {
		// userRepository.deleteAll();
		Stream.of("a k", "s s", "r k").forEach(name -> {
			String[] usrName = name.split(" ");
			User user = User.builder().firstName(usrName[0]).lastName(usrName[1]).age((int) (50 * Math.random() + 20))
					.build();
			userRepository.save(user);
		});
		userRepository.findAll().forEach(System.out::println);
	}

	@Autowired
	private StudentRepository studentRepo;

	/*
	 * Data in Redis database.
	 */
	private void testStudentRepo() {
		// studentRepo.deleteAll();
		Stream.of("1 Alice F 87", "2 Smith M 62").forEach(custStr -> {
			String[] cust = custStr.split(" ");
			studentRepo.save(Student.builder().id(cust[0]).name(cust[1])
					.gender((cust[2].equals("F") ? Student.Gender.FEMALE : Student.Gender.MALE))
					.grade(Integer.parseInt(cust[3])).build());
		});
		studentRepo.findAll().forEach(System.out::println);
	}

	@Autowired
	private ApplicationContext ctx;

	/*
	 * This is unnecessary as 1) its now by default listed in Debug mode. 2) Use
	 * /actuator/beans
	 */
	private void listBeans() {
		log.debug("Let's inspect the beans provided by Spring Boot:");
		String[] beanNames = ctx.getBeanDefinitionNames();
		Arrays.sort(beanNames);
		for (String beanName : beanNames) {
			Package packageName = ctx.getBean(beanName).getClass().getPackage();
			if (packageName != null && packageName.getName().contains("demo"))
				log.debug(beanName);
		}
	};
}
