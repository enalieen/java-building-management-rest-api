package vorlage.databaseConnection;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;


public class JDBCDBConn implements IDatabaseConnection {

	 // Object that represents the active connection to the database
    private Connection conn;

    @Override
    public Connection openConnection(String filename) {
        try {
            // Create a Properties object to read configuration from a file
            Properties props = new Properties();

            // Load all key=value pairs from the given file (for example: db.properties)
            InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
            if (in == null) {
            	throw new FileNotFoundException("Could not find" + filename + "in classpath (resources)");
            }
            props.load(in);

            // Get system username to build property keys according to Sprint 1 specification
            String systemUser = System.getProperty("user.name");

            // Get individual values from the properties file using [Systemnutzer].db.* format
            // If not found, fall back to db.* format for backwards compatibility
            String url = props.getProperty(systemUser + ".db.url", props.getProperty("db.url"));
            String user = props.getProperty(systemUser + ".db.user", props.getProperty("db.user"));
            String pw = props.getProperty(systemUser + ".db.pw", props.getProperty("db.pw"));

            // Replace ${DB_HOST} placeholder with environment variable or default value
            if (url != null && url.contains("${DB_HOST}")) {
                String dbHost = System.getenv("DB_HOST");
                if (dbHost == null || dbHost.isEmpty()) {
                    dbHost = "127.0.0.1";
                }
                url = url.replace("${DB_HOST}", dbHost);
            }

            // Connect to the database using JDBC (Java Database Connectivity)
            // DriverManager knows which driver to use (from Maven dependency)
            conn = DriverManager.getConnection(url, user, pw);
            return conn;

            // If successful, 'conn' now holds an open connection to the DB
        } catch (Exception e) {
            // Print the error details if something goes wrong (like file not found or wrong DB URL)
        	throw new RuntimeException("Failed to open connection: " + e.getMessage(), e);
        }
	}

	@Override
	public void createAllTables() {
		String createCustomer = "CREATE TABLE IF NOT EXISTS customer (" +
				"id VARCHAR(36) PRIMARY KEY, " +
				"firstName VARCHAR(100), " +
				"lastName VARCHAR(100), " +
				"gender VARCHAR(10), " +
				"birthDate DATE" +
				")";
		String createReading = "CREATE TABLE IF NOT EXISTS reading (" +
				"id VARCHAR(36) PRIMARY KEY, " +
				"customer_id VARCHAR(36), " +
				"dateOfReading DATE, " +
				"kindOfMeter VARCHAR(50), " +
				"meterCount DOUBLE, " +
				"meterId VARCHAR(100), " +
				"comment TEXT, " +
				"substitute BOOLEAN, " +
				"FOREIGN KEY (customer_id) REFERENCES customer(id) ON DELETE SET NULL" +
				")";

		try (Statement st = conn.createStatement()) {
			st.execute(createCustomer);
			st.execute(createReading);
		} catch (SQLException e) {
			throw new RuntimeException("Failed to create tables: " + e.getMessage(), e);
		}
	}

	@Override
	public void removeAllTables() {
		try (Statement st = conn.createStatement()) {
			st.execute("DROP TABLE IF EXISTS reading");
			st.execute("DROP TABLE IF EXISTS customer");
		} catch (SQLException e) {
			throw new RuntimeException("Failed to remove tables: " + e.getMessage(), e);
		}
	}

	@Override
	public void truncateAllTables() {
		try (Statement st = conn.createStatement()) {
			st.execute("SET FOREIGN_KEY_CHECKS = 0");
			st.execute("TRUNCATE TABLE reading");
			st.execute("TRUNCATE TABLE customer");
			st.execute("SET FOREIGN_KEY_CHECKS = 1");
		} catch (SQLException e) {
			throw new RuntimeException("Failed to truncate tables: " + e.getMessage(), e);
		}
	}

	@Override
	public void closeConnection() {
		try {
		if (conn != null && !conn.isClosed()) {
			conn.close();
		}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Connection getConnection() {
		return this.conn;
	}

}
