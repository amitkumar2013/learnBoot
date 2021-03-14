package com.example.demo.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.support.BaseLdapPathContextSource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.authentication.LdapAuthenticator;
import org.springframework.security.ldap.authentication.PasswordComparisonAuthenticator;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;
import org.springframework.security.ldap.server.UnboundIdContainer;
import org.springframework.security.ldap.userdetails.DefaultLdapAuthoritiesPopulator;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.extern.slf4j.Slf4j;

/**
 * This class configure the security filter chain that carries the OAuth 2.0
 * authentication processor.
 *
 * @author amit.30.kumar
 */
// With Multiple Security Configuration - 
// Use lower order for special case
// And once an authentication is successful, no other adapter is called
@Order(50)
@Slf4j
@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// Set 401 and send to '/' - default failure url.
		SimpleUrlAuthenticationFailureHandler handler = new SimpleUrlAuthenticationFailureHandler("/");

		// .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
		// .requestMatchers(PathRequest.toH2Console()).permitAll() // "/h2/**"
		http.authorizeRequests(

				// switch off the security on the home, error page & javascript jars
				a -> a.antMatchers("/", "/ping", "/error", "/webjars/**", "/h2/**", "/favicon.ico").permitAll()
					  .antMatchers("/graphql", "/graphql/*", "/graphiql", "/vendor/graphiql/*").permitAll()
						// .hasRole("ADMIN") or hasAuthority('read') or hasPermission(domainObject, 'read')
						// CUSTOM: .access("hasRole('...') and hasRole('...')") even ("@myBean.check(authentication,request)")
						.anyRequest().authenticated()) // The rest are secured
				// A 401 instead of 'redirect to login page :DEFAULT'
				.exceptionHandling(e -> e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
				// Only accept from same site (symbolised with token from session) & POST only requests.
				// Server sends out the token as "XSRF-TOKEN" and expects it back as "X-XSRF-TOKEN" - code in index.html
				.csrf(c -> c.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))

				// Avoid using Remember me - ability to login across sessions using tokens saved in cookies 
				// .rememberMe().rememberMeParameter("remember-me-new")
				// .rememberMe().key("uniqueAndSecret").tokenValiditySeconds(86400)

				// logout clears the session and invalidate the cookie.
				.logout(l -> l.logoutSuccessUrl("/").invalidateHttpSession(true).deleteCookies("JSESSIONID").permitAll())
				
				// Login-form .formLogin(withDefaults()); or .formLogin(form -> form.loginPage("/login").permitAll());
				// Basic .httpBasic(withDefaults());  Digest - not recommended
				
				// Pre configured AUTH Servers is just a matter of defining a simple oauth2Login() element
				.oauth2Login(o -> 
					// Capture any error message and set in session so that it can be later fetched.
					o.failureHandler((request, response, exception) -> {
							request.getSession().setAttribute("error.message", exception.getMessage());
							handler.onAuthenticationFailure(request, response, exception);
					}
				));
				// Multiple custom behaviours can be added to .oauth2Login()....
				
				// 1) A Custom login page --> .oauth2Login().loginPage("/oauth_login");
				// Make sure this API has access to @ClientRegistrationRepository for auth_url.
		
				// 2) A custom post login behaviour --> .oauth2Login().defaultSuccessUrl("/loginSuccess").failureUrl("/loginFailure");
				
				// 3) .authorizationEndpoint().baseUri("/oauth2/authorize-client").authorizationRequestRepository(...);
				// with custom AuthorizationRequestRepository<OAuth2AuthorizationRequest> 
		
				// 4) .tokenEndpoint().accessTokenResponseClient(...);
				// with custom OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest>
		
				// 5) .redirectionEndpoint().baseUri("/oauth2/redirect")
		
				// 6) .userInfoEndpoint()

				// Resource Server
				// TODO - .oauth2ResourceServer().jwt();//.opaqueToken()
				// Multiple custom behaviours can be added to .oauth2ResourceServer()....
		
				// 1. JWT as well as Opaque token - Both
				// 2. /userinfo
				// 3. Multi-tenancy 
				
		
	// In order to use a custom OAuth provider besides Google, Github, Okta & Facebook
	// Use InMemoryOAuth2AuthorizedClientService --> with InMemoryClientRegistrationRepository(ClientRegistration)
	// .oauth2Login().clientRegistrationRepository(clientRegistrationRepository()).authorizedClientService(authorizedClientService());
		
		// For H2 console
		http.headers().frameOptions().disable();
	}
	
	/*
	 * This bean acts as a hook to authentication flow and can implement logic to further allow/stop authentication.
	 * viz. here its looking for organisation match specific to GitHub only
	 */
	@Bean
	public OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService(WebClient rest) {
		DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
		return request -> {
			OAuth2User user = delegate.loadUser(request);
			if (!"github".equals(request.getClientRegistration().getRegistrationId())) {
				return user; // GITHUB only !!!
			}
			// Step 1: Get the client corresponding to the current user token.
			OAuth2AuthorizedClient client = new OAuth2AuthorizedClient
					(request.getClientRegistration(), user.getName(), request.getAccessToken());
			
			// Step 2: Access the User Information 
			// String userInfoEndpointUrl = client.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUri();
			
			// OR Access the Github Org information viz. https://api.github.com/users/<amitkumar2013>/orgs
			String orgInfoEndpointUrl = user.getAttribute("organizations_url"); 
			// rest.get().uri(orgInfoEndpointUrl).attributes(oauth2AuthorizedClient(client)).retrieve().bodyToMono(List.class).block();
			log.debug("Received Organizations list from Github");
			// A simple match of organisation : just an example
			
			// TO-DO : Not working; Use Dummy data for now.
			Map<String, Object> map = new HashMap<>();
			map.put("login", "spring-projects");
			List<Map<String, Object>> orgs = new ArrayList<>(); 
			orgs.add(map);
			
			if (orgs.stream().anyMatch(org -> "spring-projects".equals(org.get("login")))) {
				return user;
			}
			throw new OAuth2AuthenticationException(new OAuth2Error("invalid_token", "Not in Spring Team", ""));
		};
	}
	@Bean // The above hook works with WebClient (Reactive style i.e. with Webflux)
	public WebClient rest(ClientRegistrationRepository clients, OAuth2AuthorizedClientRepository authz) {
		ServletOAuth2AuthorizedClientExchangeFilterFunction oauth2 = new ServletOAuth2AuthorizedClientExchangeFilterFunction(clients, authz);
		return WebClient.builder().filter(oauth2).build();
	}

	// --------------------------- DATABASE ----------------------------------

	// Default - AuthenticationManagerBuilder#userDetailsService or .jdbcAuthentication(DataSource)
	
	//@Bean
	public UserDetailsService users() {
	    UserDetails user = User.builder().username("user").password("{bcrypt}$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW")
	        .roles("USER").build();
	    UserDetails admin = User.builder().username("admin").password("{bcrypt}$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW")
	        .roles("USER", "ADMIN").build();

	    // DB Or InMemory
	    UserDetailsManager userDetailsMgr = null;
	    boolean dbBackedIdentityProvider = false;
	    if(dbBackedIdentityProvider ) {
		    JdbcUserDetailsManager users = new JdbcUserDetailsManager(dataSource()); // is userDetailsService
		    users.createUser(user);users.createUser(admin);
		    userDetailsMgr = users;
	    } else {
	    	userDetailsMgr = new InMemoryUserDetailsManager(user, admin);
	    }
	    return userDetailsMgr;
	}
	//@Bean
	DataSource dataSource() {
	    return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2)
	        .addScript("classpath:org/springframework/security/core/userdetails/jdbc/users.ddl").build();
	}

	// --------------------------- LDAP ----------------------------------
	
	//@Bean
	UnboundIdContainer ldapContainer() {
	    return new UnboundIdContainer("dc=springframework,dc=org", "classpath:users.ldif");
	}
	//@Bean - Similar to dataSource
	ContextSource contextSource(UnboundIdContainer container) {
	    return new DefaultSpringSecurityContextSource("ldap://localhost:53389/dc=springframework,dc=org");
	}
	/*
	 * Spring Security’s LDAP support does not use the UserDetailsService because
	 * LDAP bind authentication does not allow clients to read the password or even
	 * a hashed version of the password.
	 * 
	 * So use LdapAuthenticator - BindAuthenticator OR PasswordComparisonAuthenticator
	 */
	//@Bean	
	PasswordComparisonAuthenticator _authenticator(BaseLdapPathContextSource contextSource) {
	    PasswordComparisonAuthenticator authenticator =  new PasswordComparisonAuthenticator(contextSource);
		authenticator.setPasswordAttributeName("pwd"); 
		authenticator.setPasswordEncoder(new BCryptPasswordEncoder()); 
		return authenticator;
	}
	//@Bean
	BindAuthenticator __authenticator(BaseLdapPathContextSource contextSource) {
	    BindAuthenticator authenticator = new BindAuthenticator(contextSource);
	    // to configure an LDAP search filter to locate the user
	    String searchBase = "ou=people";
	    String filter = "(uid={0})";
	    FilterBasedLdapUserSearch search =  new FilterBasedLdapUserSearch(searchBase, filter, contextSource);
	    authenticator.setUserSearch(search);
	    // Else vanilla
	    authenticator.setUserDnPatterns(new String[] { "uid={0},ou=people" });
	    return authenticator;
	}
	// To specify what authorities are returned for the user
	//@Bean
	LdapAuthoritiesPopulator authorities(BaseLdapPathContextSource contextSource) {
	    String groupSearchBase = "";
	    DefaultLdapAuthoritiesPopulator authorities = new DefaultLdapAuthoritiesPopulator(contextSource, groupSearchBase);
	    authorities.setGroupSearchFilter("member={0}");
	    return authorities;
	}
	//@Bean
	//LdapAuthenticationProvider authenticationProvider(LdapAuthenticator authenticator, LdapAuthoritiesPopulator authorities) {
	LdapAuthenticationProvider authenticationProvider(LdapAuthenticator authenticator) {
		//return new LdapAuthenticationProvider(authenticator, authorities);
	    return new LdapAuthenticationProvider(authenticator);
	}
	
	// ---------------------------- AD ---------------------------------------
	//@Bean
	ActiveDirectoryLdapAuthenticationProvider authenticationProvider() {
	    return new ActiveDirectoryLdapAuthenticationProvider("example.com", "ldap://company.example.com/");
	}
}










