package vorlage.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import dev.hv.model.IReading;

// Data Access Object for Reading
public class ReadingDAO {

	private final Connection conn;

	public ReadingDAO(Connection conn) {
		this.conn = conn;
	}

	// CREATE a new reading in DB
	public boolean create(Reading reading) throws SQLException {
		// Ensure reading has a customer
		if (reading.getCustomer() == null) {
			throw new IllegalArgumentException("Reading must have a customer. Cannot insert reading without customer.");
		}

		// If customer doesn't exist in DB, create it
		CustomerDAO customerDAO = new CustomerDAO(conn);
		Customer customer = (Customer) reading.getCustomer();
		Customer existingCustomer = customerDAO.findById(customer.getId());

		if (existingCustomer == null) {
			// Customer doesn't exist - create automatically
			customerDAO.create(customer);
			System.out.println("Customer not found in DB, created automatically: " + customer.getId());
		}

		// Insert the reading
		String sql = "INSERT INTO reading (id, customer_id, dateOfReading, kindOfMeter, meterCount, meterId, comment, substitute) " +
			"VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		if (reading.getId() == null) {
			reading.setId(UUID.randomUUID());
		}
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, reading.getId().toString());
			ps.setString(2, customer.getId().toString());
			ps.setDate(3, reading.getDateOfReading() != null ? Date.valueOf(reading.getDateOfReading()) : null);
			ps.setString(4, reading.getKindOfMeter() != null ? reading.getKindOfMeter().name() : null);
			ps.setDouble(5, reading.getMeterCount() != null ? reading.getMeterCount() : 0);
			ps.setString(6, reading.getMeterId());
			ps.setString(7, reading.getComment());
			ps.setBoolean(8, reading.getSubstitute() != null && reading.getSubstitute());
			return ps.executeUpdate() == 1;
		}
	}

	// READ by ID
	public Reading findById(UUID id) throws SQLException {
		String sql = "SELECT * FROM reading WHERE id = ?";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, id.toString());
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				Reading r = new Reading();
				r.setId(UUID.fromString(rs.getString("id")));
				r.setDateOfReading(rs.getDate("dateOfReading") != null ? rs.getDate("dateOfReading").toLocalDate() : null);
				r.setMeterCount(rs.getDouble("meterCount"));
				r.setMeterId(rs.getString("meterId"));
				r.setComment(rs.getString("comment"));
				r.setSubstitute(rs.getBoolean("substitute"));
				r.setKindOfMeter(rs.getString("kindOfMeter") != null ? IReading.KindOfMeter.valueOf(rs.getString("kindOfMeter")) : null);

				// Optional: load customer here if needed
				return r;
			} else {
				// Reading not found - print to console
				System.out.println("Reading not found with id: " + id);
				return null;
			}
		}
	}

	// LIST ALL readings
	public List<Reading> showAllReadings() throws SQLException {
		List<Reading> list = new ArrayList<>();
		String sql = "SELECT * FROM reading";
		try (Statement st = conn.createStatement()) {
			ResultSet rs = st.executeQuery(sql);
			while (rs.next()) {
				Reading r = new Reading();
				r.setId(UUID.fromString(rs.getString("id")));
				r.setDateOfReading(rs.getDate("dateOfReading") != null ? rs.getDate("dateOfReading").toLocalDate() : null);
				r.setMeterCount(rs.getDouble("meterCount"));
				r.setMeterId(rs.getString("meterId"));
				r.setComment(rs.getString("comment"));
				r.setSubstitute(rs.getBoolean("substitute"));
				r.setKindOfMeter(rs.getString("kindOfMeter") != null ? IReading.KindOfMeter.valueOf(rs.getString("kindOfMeter")) : null);
				list.add(r);
			}

			if (list.isEmpty()) {
				System.out.println("No readings found in database.");
			}
		}
		return list;
	}

	// DELETE reading by ID
	public boolean deleteReading(UUID id) throws SQLException {
		String sql = "DELETE FROM reading WHERE id = ?";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, id.toString());
			int affected = ps.executeUpdate();
			if (affected == 0) {
				System.out.println("No reading found to delete with id: " + id);
				return false;
			}
			return true;
		}
	}

	// UPDATE existing reading
	public boolean updateReading(Reading reading) throws SQLException {

		if (reading.getId() == null) {
			throw new IllegalArgumentException("Reading ID must not be null for update.");
		}

		String sql = "UPDATE reading SET customer_id = ?, dateOfReading = ?, kindOfMeter = ?, meterCount = ?, meterId = ?, comment = ?, substitute = ? WHERE id = ?";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {

			Customer customer = (Customer) reading.getCustomer();

			ps.setString(1, customer != null ? customer.getId().toString() : null);
			ps.setDate(2, reading.getDateOfReading() != null ? Date.valueOf(reading.getDateOfReading()) : null);
			ps.setString(3, reading.getKindOfMeter() != null ? reading.getKindOfMeter().name() : null);
			ps.setDouble(4, reading.getMeterCount() != null ? reading.getMeterCount() : 0);
			ps.setString(5, reading.getMeterId());
			ps.setString(6, reading.getComment());
			ps.setBoolean(7, reading.getSubstitute() != null && reading.getSubstitute());
			ps.setString(8, reading.getId().toString());

			return ps.executeUpdate() == 1;
		}
	}
}

