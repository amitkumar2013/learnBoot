package com.example.demo.config;

import java.util.HashMap;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.config.MongoConfigurationSupport;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDbFactory;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoClient;

/*
 * Spring Boot configures Hibernate as the default JPA provider.
 * so it's no longer necessary to define infrastructure components like 
 * a) DataSource b) EntityManagerFactory c) PlatformTransactionManager, unless we want to customize it.
 * 
 * persistence.xml is also not required - its covered by pkg scanning as configured below for @Entity
 */
@Configuration
//basePackageClasses if they are in same package
@EnableJpaRepositories(basePackages = "com.example.demo.data.struc", entityManagerFactoryRef = "h2EntityManager", transactionManagerRef = "h2TxnManager")
//@EnableJpaAuditing // Only works with @EntityListeners on Entities.
class DBConfiguration {

    @Autowired
    private Environment env;
    
	@Primary
	@Bean(name="hsqlDS")
	@ConfigurationProperties(prefix = "spring.datasource")
	public DataSource inMemoryDataSource() {
		return DataSourceBuilder.create().build();
	}
	
	@Primary
    @Bean
    public LocalContainerEntityManagerFactoryBean h2EntityManager() {
        final LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(inMemoryDataSource());
        em.setPackagesToScan("com.example.demo.data.struc");

        final HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        final HashMap<String, Object> properties = new HashMap<String, Object>();
        properties.put("hibernate.hbm2ddl.auto", env.getProperty("hibernate.hbm2ddl.auto"));
        properties.put("hibernate.dialect", env.getProperty("hibernate.dialect"));
        em.setJpaPropertyMap(properties);

        return em;
    }
    
    @Bean
    public PlatformTransactionManager h2TxnManager() {
        final JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(h2EntityManager().getObject());
        return transactionManager;
    }
}

// Besides multiple datasource how about separate TransactionManager along with its EntityManagerFactory ???
/*
 * USAGE:
 *  @Autowired
 *  @Qualifier("<hsqlDS|mySQLDS|mongoDS>")
 *  private DataSource dataSource;
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.example.demo.data.unstruc", entityManagerFactoryRef = "mysqlEntityManager", transactionManagerRef = "mysqlTxnManager")
//@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
class SecondDBConfiguration {
	@Bean(name="mySQLDS")
	@ConfigurationProperties(prefix = "spring.second-datasource")
	public DataSource mysqlDataSource() {
		//  final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        //	dataSource.setDriverClassName(Preconditions.checkNotNull(env.getProperty("jdbc.driverClassName")));
        //	dataSource.setUrl(Preconditions.checkNotNull(env.getProperty("product.jdbc.url")));
        //	dataSource.setUsername(Preconditions.checkNotNull(env.getProperty("jdbc.user")));
        //	dataSource.setPassword(Preconditions.checkNotNull(env.getProperty("jdbc.pass")));
		return DataSourceBuilder.create().build();
	}
	/*
    @Bean
    @Conditional(DataSourceCondition.class)
    public DataSourceInitializer dataSourceInitializer(DataSource dataSource) {
        ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator();
        resourceDatabasePopulator.addScript(new ClassPathResource("<path>/scripts/loadRoleData.sql"));
        DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
        dataSourceInitializer.setDataSource(dataSource);
        dataSourceInitializer.setDatabasePopulator(resourceDatabasePopulator);
        return dataSourceInitializer;
    }
    */
    @Bean
    public LocalContainerEntityManagerFactoryBean mysqlEntityManager() {
        final LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(mysqlDataSource());
        em.setPackagesToScan("com.example.demo.data.unstruc");
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        return em;
    }
    
    @Bean
    public PlatformTransactionManager mysqlTxnManager() {
        final JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(mysqlEntityManager().getObject());
        return transactionManager;
    }
}

// The Java configuration requires to define two spring beans -  
// a) MongoDbFactory, we can provide connection parameters & 
// b) MongoTemplate, which uses the MongoDbFactory
//
// MongoRepository over MongoTemplate similar to JDBCTemplate
@Configuration
@EnableMongoRepositories(basePackages = "com.example.demo.data.nosql")//, mongoTemplateRef = "mongoTemplate") // In case of multiple mongo
class NoSQLDBConfiguration {
// extends MongoConfigurationSupport would need getDatabaseName()
// extending AbstractMongoClientConfiguration would require mongoClient() & getDatabaseName() 
	
	// Auto configuration will do below commented
	// In case multiple its better to switch off auto configuration.
	// autoconfigure.exclude: org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration
	/*
    @Autowired
    private Environment env;
   
    @Bean
    public MongoDbFactory mongoDbFactory() {
		return new SimpleMongoClientDbFactory(env.getProperty("spring.data.mongodb.uri"));
	}

	@Bean
	public MongoTemplate mongoTemplate() {
		MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory());
		return mongoTemplate;
    }
    
    // Starting from the 4.0 release, MongoDB supports multi-document ACID transactions.
    @Bean
    MongoTransactionManager transactionManager(MongoDbFactory dbFactory) {
        return new MongoTransactionManager(dbFactory);
    }
    */
}
