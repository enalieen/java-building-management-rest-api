package vorlage.databaseConnection;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.Statement;


class JDBCDBConnTest {

	@Test
	void openConnection_validFile_noException() {
		JDBCDBConn db = new JDBCDBConn();

		// test.properties must be in src/test/resources
		// assertDoesNotThrow executes code or falls -> Exception
		assertDoesNotThrow(() -> db.openConnection("test.properties"));

	}

	@Test
	void openConnection_invalidFile_throwsException() {
		JDBCDBConn db = new JDBCDBConn();

		// Test: Non-existent file should throw RuntimeException
		assertThrows(RuntimeException.class, () -> {
			db.openConnection("non-existent-file.properties");
		});
	}

	@Test
	void testTruncateAllTables() throws Exception {
		JDBCDBConn db = new JDBCDBConn();
		Connection conn = db.openConnection("test.properties");

		// Create tables first
		db.createAllTables();

		// Insert some test data
		try (Statement st = conn.createStatement()) {
			st.execute("INSERT INTO customer VALUES ('test-id', 'Test', 'User', 'M', '2000-01-01')");
		}

		// Truncate should clear all data
		assertDoesNotThrow(db::truncateAllTables);

		// Verify tables are empty
		try (Statement st = conn.createStatement()) {
			var rs = st.executeQuery("SELECT COUNT(*) FROM customer");
			rs.next();
			assertEquals(0, rs.getInt(1));
		}
	}

	@Test
	void testGetConnection() {
		JDBCDBConn db = new JDBCDBConn();
		db.openConnection("test.properties");

		Connection conn = db.getConnection();
		assertNotNull(conn);
	}

	@Test
	void testCloseConnection() throws Exception {
		JDBCDBConn db = new JDBCDBConn();
		Connection conn = db.openConnection("test.properties");

		assertFalse(conn.isClosed());

		db.closeConnection();

		assertTrue(conn.isClosed());
	}

	@Test
	void testCreateAllTables() throws Exception {
		JDBCDBConn db = new JDBCDBConn();
		Connection conn = db.openConnection("test.properties");

		// Remove tables first
		db.removeAllTables();

		// Create tables
		assertDoesNotThrow(() -> db.createAllTables());

		// Verify tables exist by querying them
		try (Statement st = conn.createStatement()) {
			assertDoesNotThrow(() -> st.executeQuery("SELECT * FROM customer"));
			assertDoesNotThrow(() -> st.executeQuery("SELECT * FROM reading"));
		}
	}

	@Test
	void testCloseConnection_withNullConnection() {
		// Test: closeConnection when conn is null should not throw
		JDBCDBConn db = new JDBCDBConn();
		// Connection is null because we haven't opened one
		assertDoesNotThrow(() -> db.closeConnection());
	}

	@Test
	void testCloseConnection_alreadyClosed() throws Exception {
		// Test: closeConnection when connection is already closed
		JDBCDBConn db = new JDBCDBConn();
		Connection conn = db.openConnection("test.properties");

		// Close it once
		conn.close();
		assertTrue(conn.isClosed());

		// Call closeConnection again - should not throw
		assertDoesNotThrow(() -> db.closeConnection());
	}

	@Test
	void testOpenConnection_withoutPlaceholder() {
		// Test: Properties file without ${DB_HOST} placeholder
		JDBCDBConn db = new JDBCDBConn();

		// Should successfully connect without placeholder replacement
		assertDoesNotThrow(() -> {
			Connection conn = db.openConnection("db.properties");
			assertNotNull(conn);
			assertFalse(conn.isClosed());
		});
	}

	@Test
	void testRemoveAllTables_multipleTimes() throws Exception {
		// Test: removeAllTables can be called multiple times without error
		JDBCDBConn db = new JDBCDBConn();
		db.openConnection("test.properties");

		// First call
		assertDoesNotThrow(db::removeAllTables);

		// Second call - tables don't exist anymore, but DROP IF EXISTS should handle it
		assertDoesNotThrow(db::removeAllTables);
	}

	@Test
	void testCreateAllTables_withClosedConnection_throwsException() throws Exception {
		// Test: createAllTables with closed connection should throw RuntimeException
		JDBCDBConn db = new JDBCDBConn();
		Connection conn = db.openConnection("test.properties");

		// Close the connection to provoke SQLException
		conn.close();

		// Should throw RuntimeException wrapping SQLException
		assertThrows(RuntimeException.class, db::createAllTables);
	}

	@Test
	void testRemoveAllTables_withClosedConnection_throwsException() throws Exception {
		// Test: removeAllTables with closed connection should throw RuntimeException
		JDBCDBConn db = new JDBCDBConn();
		Connection conn = db.openConnection("test.properties");

		// Close the connection to provoke SQLException
		conn.close();

		// Should throw RuntimeException wrapping SQLException
		assertThrows(RuntimeException.class, db::removeAllTables);
	}

	@Test
	void testTruncateAllTables_withClosedConnection_throwsException() throws Exception {
		// Test: truncateAllTables with closed connection should throw RuntimeException
		JDBCDBConn db = new JDBCDBConn();
		Connection conn = db.openConnection("test.properties");

		// Close the connection to provoke SQLException
		conn.close();

		// Should throw RuntimeException wrapping SQLException
		assertThrows(RuntimeException.class, db::truncateAllTables);
	}

}

