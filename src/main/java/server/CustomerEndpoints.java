package server;

import io.javalin.Javalin;
import vorlage.model.Customer;
import vorlage.model.CustomerDAO;

import java.sql.SQLException;
import java.util.UUID;

public class CustomerEndpoints {

	private final CustomerDAO customerDao;

	public CustomerEndpoints(CustomerDAO customerDao) {
		this.customerDao = customerDao;
	}

	public void register(Javalin app) {

		app.get("/customers", ctx ->
			ctx.json(customerDao.showAllCustomers())
		);

		app.get("/customers/{id}", ctx -> {
			try {
				UUID id = UUID.fromString(ctx.pathParam("id"));
				Customer customer = customerDao.findById(id);

				if (customer != null) {
					ctx.json(customer);
				} else {
					ctx.status(404).result("Customer not found");
				}

			} catch (IllegalArgumentException e) {
				ctx.status(400).result("Invalid UUID format");
			}
		});
		app.post("/customers", ctx -> {
			try {
				Customer customer = ctx.bodyAsClass(Customer.class);
				boolean created = customerDao.create(customer);

				if (created) {
					ctx.status(201).json(customer);
				} else {
					ctx.status(400).result("Customer could not be created");
				}

			} catch (SQLException e) {
				if (e.getErrorCode() == 1062) {
					ctx.status(409).result("Customer with this ID already exists");
				} else {
					ctx.status(500).result("Database error: " + e.getMessage());
				}
			} catch (IllegalArgumentException e) {
				ctx.status(400).result("Invalid UUID format or invalid gender");
			} catch (Exception e) {
				ctx.status(400).result("Invalid request body");
			}
		});

		app.delete("/customers/{id}", ctx -> {
			try {
				UUID id = UUID.fromString(ctx.pathParam("id"));
				boolean deleted = customerDao.deleteCustomer(id);

				if (deleted) {
					ctx.status(204);
				} else {
					ctx.status(404).result("Customer not found");
				}

			} catch (IllegalArgumentException e) {
				ctx.status(400).result("Invalid UUID format");
			}
		});
	}
}
