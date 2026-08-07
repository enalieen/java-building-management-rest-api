package vorlage.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import vorlage.databaseConnection.JDBCDBConn;

class ReadingDAOTest {

    private ReadingDAO dao;
    private Reading reading;
    private Customer customer;

    @BeforeEach
    void setup() throws Exception {
        JDBCDBConn db = new JDBCDBConn();
        Connection conn = db.openConnection("db.properties");
        db.removeAllTables();
        db.createAllTables();

        dao = new ReadingDAO(conn);

        customer = new Customer(UUID.randomUUID(), "Mariia", "Buashuieva", Customer.Gender.W, LocalDate.of(2000,2,16));
        reading = new Reading();
        reading.setId(UUID.randomUUID());
        reading.setCustomer(customer);
        reading.setDateOfReading(LocalDate.now());
        reading.setKindOfMeter(Reading.KindOfMeter.STROM);
        reading.setMeterCount(123.45);
        reading.setMeterId("Meter-001");
        reading.setComment("Test reading");
        reading.setSubstitute(false);
    }

    @Test
    void testCreateAndFindReading() throws Exception {
        assertTrue(dao.create(reading));

        Reading found = dao.findById(reading.getId());
        assertNotNull(found);
        assertEquals(reading.getMeterCount(), found.getMeterCount());
        assertEquals(reading.getKindOfMeter(), found.getKindOfMeter());
    }

    @Test
    void testDeleteReading() throws Exception {
        dao.create(reading);
        assertTrue(dao.deleteReading(reading.getId()));

        Reading found = dao.findById(reading.getId());
        assertNull(found);
    }

    @Test
    void testShowAllReadings() throws Exception {
        dao.create(reading);

        List<Reading> readings = dao.showAllReadings();

		//problem here: it checks if there is one (!) specific reading in the list of readings. if we save multiple -> error
//        assertEquals(1, readings.size());
//        assertEquals(reading.getMeterId(), readings.get(0).getMeterId());

		//fixed option:
		boolean readingExists = readings.stream().anyMatch(r-> r.getId().equals(reading.getId()) &&
			r.getMeterId().equals(reading.getMeterId()));
		assertTrue(readingExists, "The created reading extsts");
    }

    @Test
    void testCreateReadingWithoutCustomer() throws Exception {
        // Test: Reading without customer should throw IllegalArgumentException
        Reading readingWithoutCustomer = new Reading();
        readingWithoutCustomer.setId(UUID.randomUUID());
        readingWithoutCustomer.setCustomer(null);
        readingWithoutCustomer.setDateOfReading(LocalDate.now());
        readingWithoutCustomer.setKindOfMeter(Reading.KindOfMeter.WASSER);
        readingWithoutCustomer.setMeterCount(50.0);

        assertThrows(IllegalArgumentException.class, () -> {
            dao.create(readingWithoutCustomer);
        });
    }

    @Test
    void testCreateReadingWithExistingCustomer() throws Exception {
        // Test: Customer already exists, should NOT auto-create
        JDBCDBConn db = new JDBCDBConn();
        Connection conn = db.openConnection("db.properties");
        db.removeAllTables();
        db.createAllTables();

        CustomerDAO customerDAO = new CustomerDAO(conn);
        ReadingDAO readingDAO = new ReadingDAO(conn);

        // First create the customer manually
        customerDAO.create(customer);

        // Now create reading - customer should already exist (not auto-created again)
        Reading newReading = new Reading();
        newReading.setId(UUID.randomUUID());
        newReading.setCustomer(customer);
        newReading.setDateOfReading(LocalDate.now());
        newReading.setKindOfMeter(Reading.KindOfMeter.HEIZUNG);
        newReading.setMeterCount(200.0);
        newReading.setMeterId("EXISTING-CUSTOMER-METER");
        newReading.setComment("Test existing customer");
        newReading.setSubstitute(false);

        assertTrue(readingDAO.create(newReading));

        Reading found = readingDAO.findById(newReading.getId());
        assertNotNull(found);
        assertEquals(newReading.getMeterId(), found.getMeterId());
    }

    @Test
    void testCreateReadingWithNullValues() throws Exception {
        // Test: Reading with null optional fields
        Reading readingWithNulls = new Reading();
        readingWithNulls.setId(UUID.randomUUID());
        readingWithNulls.setCustomer(customer);
        readingWithNulls.setDateOfReading(null);
        readingWithNulls.setKindOfMeter(null);
        readingWithNulls.setMeterCount(null);
        readingWithNulls.setMeterId("NULL-METER");
        readingWithNulls.setComment("Test null values");
        readingWithNulls.setSubstitute(null);

        assertTrue(dao.create(readingWithNulls));

        Reading found = dao.findById(readingWithNulls.getId());
        assertNotNull(found);
        assertNull(found.getDateOfReading());
    }

    @Test
    void testDeleteNonExistentReading() throws Exception {
        // Test: Delete a reading that doesn't exist
        UUID nonExistentId = UUID.randomUUID();
        assertFalse(dao.deleteReading(nonExistentId));
    }
}
