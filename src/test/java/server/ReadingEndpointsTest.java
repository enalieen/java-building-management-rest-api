package server;

import dev.hv.model.ICustomer;
import dev.hv.model.IReading;
import io.javalin.Javalin;
import org.junit.jupiter.api.*;
import vorlage.databaseConnection.JDBCDBConn;
import vorlage.model.Customer;
import vorlage.model.Reading;
import vorlage.model.ReadingDAO;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

class ReadingEndpointsTest {
	private Javalin app;
	private ReadingDAO readingDAO;
	private Connection conn;
	@BeforeEach
	void setup() throws Exception {
		JDBCDBConn db = new JDBCDBConn();
		db.openConnection("db.properties"); // open real or test database connection
		db.removeAllTables();               // drop all existing tables
		db.createAllTables();               // recreate customer and reading tables

		app = Javalin.create();
		new ReadingEndpoints(readingDAO).register(app);
		app.start(0); // random free port
		conn = db.getConnection();
		readingDAO = new ReadingDAO(conn);
		conn.setAutoCommit(false);

	}
	@AfterEach
	void stopServer() throws Exception {
		if (app != null) app.stop();
		if (conn != null && !conn.isClosed()) {
			conn.rollback();
			conn.setAutoCommit(true);
			conn.close();
		}
	}

//helper functions
	private Customer testCustomer() { return new Customer( UUID.randomUUID(), "FirstName", "LastName", ICustomer.Gender.M, LocalDate.of(1990, 1, 1) ); }
	private Reading testReading(Customer customer) {
		return new Reading(
			UUID.randomUUID(),
			customer,
			LocalDate.now(),
			IReading.KindOfMeter.STROM,
			123.45,
			"MeterX",
			"Test reading",
			false
		);
	}
	@Test
	void getAllReadingsReturns200() throws SQLException {
		// Create Customer
		Customer testCustomer = testCustomer();
		// Create Reading
		Reading testReading = testReading(testCustomer);

		// Insert into DB
		readingDAO.create(testReading);

		// Call endpoint
		given()
			.port(app.port())
			.when()
			.get("/readings")
			.then()
			.statusCode(200);
	}

	@Test
	void getReadingByIdReturns404WhenNotFound() {
		UUID id = UUID.randomUUID();

		given()
			.port(app.port())
			.when()
			.get("/readings/" + id)
			.then()
			.statusCode(404)
			.body(equalTo("Reading not found"));
	}

	@Test
	void postReadingReturns201() throws SQLException {
		Customer testCustomer = testCustomer();
		Reading testReading = testReading(testCustomer);

		readingDAO.create(testReading); // creating with DAO
		given()
			.port(app.port())
			.contentType("application/json")
			.body(testReading)
			.when()
			.post("/readings")
			.then()
			.statusCode(201);
	}

	@Test
	void deleteReadingReturns204() throws SQLException {
		Customer testCustomer = testCustomer();
		Reading testReading = testReading(testCustomer);

		// Insert into DB
		readingDAO.create(testReading);

		// Delete via endpoint
		given()
			.port(app.port())
			.when()
			.delete("/readings/" + testReading.getId())
			.then()
			.statusCode(204);
	}

	@Test
	void getReadingByIdReturns400ForInvalidUUID() {
		given()
			.port(app.port())
			.when()
			.get("/readings/invalid-uuid")
			.then()
			.statusCode(400)
			.body(equalTo("Invalid UUID format"));
	}
}
