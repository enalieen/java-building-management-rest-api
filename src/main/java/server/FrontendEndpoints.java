package server;

import io.javalin.Javalin;
import vorlage.model.Customer;
import vorlage.model.CustomerDAO;

import java.util.List;

public class FrontendEndpoints {

	// DAO object used to access database layer
	private final CustomerDAO customerDao;

	// Constructor injection
	// FrontendEndpoints receives DAO from Server class
	public FrontendEndpoints(CustomerDAO customerDao) {
		this.customerDao = customerDao;
	}

	// Register all frontend-related endpoints
	public void register(Javalin app) {

		// EXPORT JSON
		// Registers HTTP GET endpoint
		// Example URL:
		// http://localhost:8080/export/json
		app.get("/export/json", ctx -> {

			try {
				// DAO loads all customers from database
				// Returns List<Customer>
				List<Customer> customers = customerDao.showAllCustomers();

				// Javalin automatically converts Java objects into JSON
				// Content-Type becomes application/json
				ctx.json(customers);

			} catch (Exception e) {

				// Prints stacktrace into console for debugging
				e.printStackTrace();

				// Sends HTTP status code 500 (Internal Server Error)
				ctx.status(500).result("Export JSON failed");
			}
		});

		// EXPORT CSV

		app.get("/export/csv", ctx -> {

			try {

				// Load all customers from database
				List<Customer> customers = customerDao.showAllCustomers();

				// StringBuilder is more memory efficient than String concatenation
				// especially inside loops
				StringBuilder csv = new StringBuilder();

				// First CSV line = header row
				csv.append("id,firstName,lastName,birthDate,gender\n");

				// Convert each customer object into CSV format
				for (Customer c : customers) {

					csv.append(c.getId()).append(",")
						.append(c.getFirstName()).append(",")
						.append(c.getLastName()).append(",")
						.append(c.getBirthDate()).append(",")
						.append(c.getGender())
						.append("\n");
				}

				// Tells browser this is CSV content
				ctx.contentType("text/csv");

				// Sends generated CSV string as HTTP response
				ctx.result(csv.toString());

			} catch (Exception e) {

				e.printStackTrace();

				ctx.status(500).result("Export CSV failed");
			}
		});


		// EXPORT XML
		app.get("/export/xml", ctx -> {

			try {

				// Load all customer records from database
				List<Customer> customers = customerDao.showAllCustomers();

				// Manual XML generation
				// Simple solution without additional XML libraries
				StringBuilder xml = new StringBuilder();

				// Root XML tag
				xml.append("<customers>");

				// Convert each customer into XML structure
				for (Customer c : customers) {

					xml.append("<customer>")
						.append("<id>").append(c.getId()).append("</id>")
						.append("<firstName>").append(c.getFirstName()).append("</firstName>")
						.append("<lastName>").append(c.getLastName()).append("</lastName>")
						.append("<birthDate>").append(c.getBirthDate()).append("</birthDate>")
						.append("<gender>").append(c.getGender()).append("</gender>")
						.append("</customer>");
				}

				// Closing root tag
				xml.append("</customers>");

				// Browser understands response as XML
				ctx.contentType("application/xml");

				// Send XML as HTTP response
				ctx.result(xml.toString());

			} catch (Exception e) {
				e.printStackTrace();
				ctx.status(500).result("Export XML failed");
			}
		});
	}
}
