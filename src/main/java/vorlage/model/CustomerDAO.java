// DAO - Data Access Object for Customer

package vorlage.model;

import dev.hv.model.ICustomer.Gender;

import java.sql.*;
import java.sql.Date;
import java.util.*;

// Customer Data Access Object
public class CustomerDAO {

	// "final" variable cannot be altered after creation
	private final Connection conn;

	public CustomerDAO(Connection conn) {
		this.conn = conn;
	}

	/* CREATE: Inserts a new Customer into the database
	   Uses PreparedStatement to prevent SQL injection */
	public boolean create(Customer c) throws SQLException {
		// If ID is null, generate a new random UUID
		if (c.getId() == null) {
			c.setId(UUID.randomUUID());
		}

		String sql = "INSERT INTO customer (id, birthDate, firstName, lastName, gender) VALUES (?,?,?,?,?)";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, c.getId().toString());
			ps.setDate(2, Date.valueOf(c.getBirthDate()));
			ps.setString(3, c.getFirstName());
			ps.setString(4, c.getLastName());
			ps.setString(5, c.getGender().toString());

			boolean inserted = ps.executeUpdate() > 0;
			if (!inserted) {
				System.out.println("Customer was not inserted");
			}
			return inserted;
		}
	}

	/* READ: Find a Customer by UUID */
	public Customer findById(UUID id) throws SQLException {
		String sql = "SELECT * FROM customer WHERE id = ?";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, id.toString());
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				return new Customer(
					UUID.fromString(rs.getString("id")),
					rs.getString("firstName"),
					rs.getString("lastName"),
					Gender.valueOf(rs.getString("gender").toUpperCase()),
					rs.getDate("birthDate").toLocalDate()
				);
			} else {
				System.out.println("Customer with id " + id + " not found");
				return null;
			}
		}
	}

	/* UPDATE: Update an existing Customer */
	public boolean updateCustomer(Customer c) throws SQLException {
		String sql = "UPDATE customer SET firstName = ?, lastName = ?, gender = ?, birthDate = ? WHERE id = ?";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, c.getFirstName());
			ps.setString(2, c.getLastName());
			ps.setString(3, c.getGender().name());
			ps.setDate(4, Date.valueOf(c.getBirthDate()));
			ps.setString(5, c.getId().toString());

			boolean updated = ps.executeUpdate() > 0;
			if (!updated) {
				System.out.println("Customer with id " + c.getId() + " not found to update");
			}
			return updated;
		}
	}

	/* DELETE: Delete a Customer by UUID */
	public boolean deleteCustomer(UUID id) throws SQLException {
		String sql = "DELETE FROM customer WHERE id = ?";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, id.toString());

			boolean deleted = ps.executeUpdate() > 0;
			if (!deleted) {
				System.out.println("Customer with id " + id + " not found to delete");
			}
			return deleted;
		}
	}

	/* LIST ALL: Return all customers in the database */
	public List<Customer> showAllCustomers() throws SQLException {
		List<Customer> customers = new ArrayList<>();
		String sql = "SELECT * FROM customer";

		try (Statement st = conn.createStatement()) {
			ResultSet rs = st.executeQuery(sql);

			while (rs.next()) {
				customers.add(new Customer(
					UUID.fromString(rs.getString("id")),
					rs.getString("firstName"),
					rs.getString("lastName"),
					Gender.valueOf(rs.getString("gender").toUpperCase()),
					rs.getDate("birthDate").toLocalDate()
				));
			}
		}

		if (customers.isEmpty()) {
			System.out.println("No customers found in the database");
		}

		return customers;
	}
}
