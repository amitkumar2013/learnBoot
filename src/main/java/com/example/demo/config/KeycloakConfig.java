package com.example.demo.config;

import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.keycloak.adapters.springsecurity.filter.KeycloakAuthenticatedActionsFilter;
import org.keycloak.adapters.springsecurity.filter.KeycloakAuthenticationProcessingFilter;
import org.keycloak.adapters.springsecurity.filter.KeycloakPreAuthActionsFilter;
import org.keycloak.adapters.springsecurity.filter.KeycloakSecurityContextRequestFilter;
import org.keycloak.adapters.springsecurity.management.HttpSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

//@Order(1)
//Avoid @Order - as everything should go via Keycloak only special cases have lower order. 
@EnableWebSecurity
//Method level security using @RoleAllowed viz.
// @Secured("IS_AUTHENTICATED_ANONYMOUSLY OR ROLE_TELLER")
// @PreAuthorize("#contact.name == authentication.name")
@EnableGlobalMethodSecurity(jsr250Enabled = true, prePostEnabled = true, securedEnabled = true) 
//@KeycloakConfiguration - explore
public class KeycloakConfig extends KeycloakWebSecurityConfigurerAdapter {

	// Authentication : User --> Roles --------------------------------------------------------------------------------------------
	// For Custom Role names.
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		// By Default, the SpringAuthorityMapper looks for roles prefix:ROLE_, SimpleAuthorityMapper guide it to go away.
		// OR simpleAuthorityMapper().setPrefix("ROLE_");
		// Anyways, Register the KeycloakAuthenticationProvider with the authentication manager
		KeycloakAuthenticationProvider keycloakAuthenticationProvider = keycloakAuthenticationProvider();
		keycloakAuthenticationProvider.setGrantedAuthoritiesMapper(new SimpleAuthorityMapper());
		auth.authenticationProvider(keycloakAuthenticationProvider);
	}

	// For multi-tenancy
	// Enables Keycloak, the SpringSecurityAdapter looks for keycloak.json config-file, guiding it to Spring.
	@Bean
	public KeycloakSpringBootConfigResolver KeycloakConfigResolver() {
		return new KeycloakSpringBootConfigResolver();
	}

	@Bean
	@Override
	protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
		// Specifies the session authentication strategy 
		// 		RegisterSessionAuthenticationStrategy for public or confidential 
		// 		NullAuthenticatedSessionStrategy for bearer-only aka B2B
		return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
	}

	// Authorization : Role -> Access --------------------------------------------------------------------------------------------
	// Keycloak sends back to the application an IdToken with the information about the identity of the user. 
	// It also provides an AccessToken containing the information relevant to the authorization of the user, including the user roles.
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		super.configure(http);
		http
			.authorizeRequests()
				// Access Control HERE 
				// UI
				.antMatchers("/greet").hasRole("user")
				.antMatchers("/about-us").permitAll()
				// API - Permit here and implement it at method level
//				.antMatchers("/contactus").permitAll()
//				.antMatchers("/").hasRole("user")
//				.antMatchers("/admin").hasRole("admin")
//				.antMatchers("/home").hasAnyRole("user","admin")
				.anyRequest().permitAll() 
				// OR the same can go in application.yml 
			.and()
				.csrf().disable()
				.headers().frameOptions().disable();
	}
	
	// It may be necessary to add FilterRegistrationBeans to your security configuration 
	// to prevent the Keycloak filters from being registered twice.
	@Bean
	public FilterRegistrationBean keycloakAuthenticationProcessingFilterRegistrationBean(
			KeycloakAuthenticationProcessingFilter filter) {
		FilterRegistrationBean registrationBean = new FilterRegistrationBean(filter);
		registrationBean.setEnabled(false);
		return registrationBean;
	}
	@Bean
	public FilterRegistrationBean keycloakPreAuthActionsFilterRegistrationBean(KeycloakPreAuthActionsFilter filter) {
		FilterRegistrationBean registrationBean = new FilterRegistrationBean(filter);
		registrationBean.setEnabled(false);
		return registrationBean;
	}
	@Bean
	public FilterRegistrationBean keycloakAuthenticatedActionsFilterBean(KeycloakAuthenticatedActionsFilter filter) {
		FilterRegistrationBean registrationBean = new FilterRegistrationBean(filter);
		registrationBean.setEnabled(false);
		return registrationBean;
	}
	@Bean
	public FilterRegistrationBean keycloakSecurityContextRequestFilterBean(
			KeycloakSecurityContextRequestFilter filter) {
		FilterRegistrationBean registrationBean = new FilterRegistrationBean(filter);
		registrationBean.setEnabled(false);
		return registrationBean;
	}
	@Bean
    @Override
    @ConditionalOnMissingBean(HttpSessionManager.class)
    protected HttpSessionManager httpSessionManager() {
        return new HttpSessionManager();
    }
}
