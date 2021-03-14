The project is a demo for Spring Boot features:

- Java 11
- Command Runnner
- DTO
- ReST API
- Configuration on DB, Scheduling, Validation
- Structured & Non-structured DB
- Junit 
- SONAR
- Build Trigger
- JavaMelody

# ------------------ STEPS ------------------------------------------
Steps:
1. Execute a mysql container:	../docker-samples/db/mysql:		docker-compose up
	a. test-user:test-password@localhost:3306/testdb
	b. create/populate a 'product' table as per the src/main/resources/schema-mysql.sql
2. Execute a solr host:			../docker-samples/search/solr:	docker-compose -f solr-compose.yml up
	a. http://127.0.0.1:8983/solr
	b. create a core 'my_core' - one may create multiple cores as well.
	c. Data is loaded with CmdRunner from /raw/data
3. Execute a keycloak container	../docker-samples/auth: 		docker-compose -f compose-keycloak.yml up
	a. Configure realm, client with callback url, roles & users.
4. Execute a redis container 	../docker-samples/db/redis: 	docker-compose -f compose-redis.yml up
5. Execute a zipkins container: ../docker-samples/monitoring/:	docker-compose -f zipkins.yml up
OPTIONAL:
6. Now try Prometheus, Grafana to capture metrics from Application, Container, Node. 	

-- http://localhost:8080/index.html
  # Spring comes with a pre-configured implementation of ResourceHttpRequestHandler to facilitate serving static resources.
  # By default, this handler serves static content from any of /static, /public, /resources, and /META-INF/resources on the classpath.

If SESSIONID is enabled, if one request is successfully authenticated, all subsequent requests are a success irrespective of passing wrong credentials

# ------------------ GraphQL ------------------------------------------
http://localhost:8080/graphiql

mutation {
	createVehicle(type: "car", modelCode: "XYZ0192", brandName: "XYZ", launchDate: "2016-08-16") 
	{
		id
	}
}

and...

query {
	vehicles(count: 1) 
	{
		id, 
		type, 
		modelCode
	}
}

or...
{
	pets 
	{
		name
		age
		type
	}
}

{
  __type(name: "PetAnimal") {
    enumValues {
      name
    }
  }
}

subscription {
  vehiclePrice(modelCode: "mazda") {
    price
  }
}
# ------------------ OAuth 2 () ------------------------------------------
Authorization Code flow:

1. GET /authorize?response_type=code&client_id=s6BhdRkqt3&state=xyz&redirect_uri=https%3A%2F%2Fclient%2Eexample%2Ecom%2Fcb HTTP/1.1
    Host: server.example.com
2. HTTP/1.1 302 Found
    Location: https://client.example.com/cb?[code=SplxlOBeZQQYbYS6WxSbIA|error=access_denied]&state=xyz
3. POST /token HTTP/1.1
    Host: server.example.com
    Authorization: Basic czZCaGRSa3F0MzpnWDFmQmF0M2JW
    Content-Type: application/x-www-form-urlencoded
    grant_type=authorization_code&code=SplxlOBeZQQYbYS6WxSbIA&redirect_uri=https%3A%2F%2Fclient%2Eexample%2Ecom%2Fcb
4. HTTP/1.1 200 OK
    Content-Type: application/json;charset=UTF-8
    Cache-Control: no-store
    Pragma: no-cache
    {
      "access_token":"2YotnFZFEjr1zCsicMWpAA",
      "token_type":"example",
      "expires_in":3600,
      "refresh_token":"tGzv3JOkF0XG5Qx2TlKWIA",
      "example_parameter":"example_value"
    }

GitHub can act as a Resource Server when asked for User Info with Access Token.
Spring-Boot app will insert the user details into the Spring Security context on successful response.
Its also set in Cookie Header: JSESSIONID

# ------------------ Tracing ----------------------------------------------
https://cloud.spring.io/spring-cloud-sleuth/reference/html

# ------------------ Log Security via masking -----------------------------
Source: https://github.com/javabeanz/owasp-security-logging/wiki

Use marker in the code:
LOGGER.info(SecurityMarkers.CONFIDENTIAL, "password={}", password);

Credit Card replacement:
<pattern>%-5level - %replace(%msg)
{'[1-6][0-9]{3}[\s-]?[0-9]{4}[\s-]?[0-9]{4}[\s-]?[0-9]{4}|5[1-5][0-9]{2}[\s-]?[0-9]{4}[\s-]?[0-9]{4}[\s-]?[0-9]{4}', 'XXXX'}%n
</pattern>
Last 4 digits replacement:
<pattern>%-5level - %replace(%msg){'\d{12,19}', 'XXXX'}%n</pattern>
         %d [%t] $logger - %replace(%msg){"pswd='.*'", "pswd='xxx'"}%n
                  
MaskingPatternLayout viz. in appender...<encoder class="ch.qos.logback.core.encoder.LayoutWrappingEncoder">
	<layout class="com.example.springboot.MaskingPatternLayout">
    		<patternsProperty>(?:user|Password)="([a-zA-Z0-9]+)"</patternsProperty>
        <pattern>%d [%thread] %-5level %logger{35} - %msg%n</pattern>
    </layout>

org.apache.logging.log4j.core.appender.rewrite.RewritePolicy;

PII - https://blog.monkey.codes/masking-pii-in-logs/

# ------------------ ----------------------------- -----------------------------
