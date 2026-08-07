package vorlage.databaseConnection;

import java.sql.Connection;

public interface IDatabaseConnection {
	
	/**
	 * Stellt eine Datenbankverbindung mit den hinterlegten Eintragungen in der Properties-Datei her
	 */
	Connection openConnection(String filename);
	
	/**
	 * Erstellt alle notwendigen Tabellen in der Datenbank
	 */
	void createAllTables();
	
	/**
	 * Loescht alle Tabellen in der Datenbank
	 */
	void removeAllTables();
	
	/**
	 * Fuehrt ein truncate bei allen Tabellen in der Datenbank durch
	 */
	void truncateAllTables();
	
	/**
	 * Schliesst die Verbindung zu Datenbank
	 */
	void closeConnection();
	
	/**
	 * Gibt das SQL-Connection Objekt der Datenbankverbindung zurueck
	 * @return
	 */
	Connection getConnection();

}