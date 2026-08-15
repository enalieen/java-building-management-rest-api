package vorlage.databaseConnection;

import java.sql.Connection;

public interface IDatabaseConnection {
	/**
	 * Establishes a database connection using the settings
	 * specified in the properties file.
	 */
	Connection openConnection(String filename);

	/**
	 * Creates all necessary tables in the database.
	 */
	void createAllTables();

	/**
	 * Deletes all tables from the database.
	 */
	void removeAllTables();

	/**
	 * Truncates all tables in the database.
	 */
	void truncateAllTables();

	/**
	 * Closes the database connection.
	 */
	void closeConnection();

	/**
	 * Returns the SQL Connection object of the database connection.
	 *
	 * @return the database connection
	 */
	Connection getConnection();
}
