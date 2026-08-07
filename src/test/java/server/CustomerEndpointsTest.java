package server;

import io.javalin.Javalin;
import io.restassured.RestAssured;
import org.junit.jupiter.api.*;
import vorlage.databaseConnection.JDBCDBConn;
import vorlage.model.CustomerDAO;
import vorlage.model.Customer;

import java.time.LocalDate;
import java.util.UUID;

import java.sql.Connection;

import static io.restassured.RestAssured.given;

class CustomerEndpointsTest {

	private Javalin app;
	private JDBCDBConn db;

	@BeforeEach
	void setup() {

		db = new JDBCDBConn();
		Connection conn = db.openConnection("db.properties");

		db.removeAllTables();
		db.createAllTables();

		CustomerDAO customerDAO = new CustomerDAO(conn);

		app = Javalin.create();
		new CustomerEndpoints(customerDAO).register(app);

		app.start(0);
		RestAssured.port = app.port();
	}

	@AfterEach
	void tearDown() {
		app.stop();
		db.closeConnection();
	}

	@Test
	void getCustomersReturns200() {
		given()
			.when()
			.get("/customers")
			.then()
			.statusCode(200);
	}

	@Test
	void getCustomerWithInvalidUUIDReturns400() {
		given()
			.when()
			.get("/customers/not-a-uuid")
			.then()
			.statusCode(400);
	}

	@Test
	void deleteNonExistingCustomerReturns404() {
		given()
			.when()
			.delete("/customers/00000000-0000-0000-0000-000000000000")
			.then()
			.statusCode(404);
	}
	@Test
	void postCustomerCreatesCustomer() {

		String id = UUID.randomUUID().toString();
		String json = """
        {
          "id": "%s",
          "firstName": "Mariia",
          "lastName": "Bushuieva",
          "gender": "W",
          "birthDate": "2000-02-16"
        }
        """.formatted(id);

		// POST
		given()
			.body(json)
			.header("Content-Type", "application/json")
			.when()
			.post("/customers")
			.then()
			.statusCode(201)
			.body("firstName", org.hamcrest.Matchers.equalTo("Mariia"))
			.body("id", org.hamcrest.Matchers.equalTo(id));

		// GET check
		given()
			.when()
			.get("/customers/" + id)
			.then()
			.statusCode(200)
			.body("firstName", org.hamcrest.Matchers.equalTo("Mariia"))
			.body("id", org.hamcrest.Matchers.equalTo(id));
	}
	@Test
	void deleteExistingCustomerReturns204() throws Exception {
		Customer cus = new Customer(UUID.randomUUID(), "Test", "User", Customer.Gender.D, LocalDate.of(1990, 1, 1));
		CustomerDAO dao = new CustomerDAO(db.openConnection("db.properties"));
		dao.create(cus);

		given()
			.when()
			.delete("/customers/" + cus.getId())
			.then()
			.statusCode(204);

		// check that GET /customers/{id} returns 404
		given()
			.when()
			.get("/customers/" + cus.getId())
			.then()
			.statusCode(404);
	}
}
