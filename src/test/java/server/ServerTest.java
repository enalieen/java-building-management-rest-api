package server;

import io.restassured.RestAssured;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;


// 8081 port for tests, 8080 localhost
class ServerTest {
@BeforeAll
	// Static = method belongs to the class, without creating an object
	static void startServer() {
		// We are testing the API behavior (not the launch process), so we are using a real server.
		Server.start(8081);
		// not to write the full URL every time in tests
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = 8081;
	}

	@AfterAll
	static void stopServer() {
		Server.stop();
	}

// endpoints
	@Test
	void testResourcesEndpointWorks() {
		given().when().get("/test/resources").then().statusCode(200).body(equalTo("API works"));
	}

	/*@Test
	// + to validate with json schema (plugin)
	void test allCustomersGet(){
	;
	}*/
}
