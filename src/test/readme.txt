@ExtendWith(SpringExtension.class) -- is the new one.

 This would NOT load the whole application context; instead to test only the web layer.
 We save the cost of starting a server -- just mock only the required layer or controller
 
 @RunWith(JUnitPlatform.class) OR @RunWith(SpringRunner|SpringJUnit4ClassRunner.class) - Junit 4 or Mockito
 In Junit5 use @ExtendWith(SpringExtension.class) or @ExtendWith(MockitoExtension.class)
 This is no longer required now from boot 2.1+
 
 Specific tests - 
 	@WebMvcTest or 
 	@RestClientTest 
 	@DataJpaTest 
- optionally specify specific controller.

$mvn -Dgroups="integration, feature-168" or $ mvn -DexcludedGroups="slow"

 