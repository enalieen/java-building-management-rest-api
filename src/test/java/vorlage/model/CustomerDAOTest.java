package vorlage.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import vorlage.databaseConnection.JDBCDBConn;

class CustomerDAOTest {

	private CustomerDAO dao;
	private Customer cus;

	@BeforeEach
	void setup() {
		JDBCDBConn db = new JDBCDBConn();
		Connection conn = db.openConnection("db.properties");
		//db.removeAllTables();
		db.createAllTables();

		dao = new CustomerDAO(conn);

		cus = new Customer(UUID.randomUUID(), "Mariia", "Buashuieva", Customer.Gender.W, LocalDate.of(2000, 2, 16));
	}

	@Test
	void testCreateAndFindCustomer() throws Exception {
		assertTrue(dao.create(cus));

		Customer found = dao.findById(cus.getId());
		assertNotNull(found);
		assertEquals(cus.getFirstName(), found.getFirstName());
		assertEquals(cus.getGender(), found.getGender());
	}

	@Test
	void testUpdateCustomer() throws Exception {
		dao.create(cus);
		cus.setFirstName("NewName");
		assertTrue(dao.updateCustomer(cus));

		Customer found = dao.findById(cus.getId());
		assertEquals("NewName", found.getFirstName());
	}

	@Test
	void testDeleteCustomer() throws Exception {
		dao.create(cus);
		assertTrue(dao.deleteCustomer(cus.getId()));

		Customer found = dao.findById(cus.getId());
		assertNull(found);
	}

	@Test
	void testShowAllCustomers() throws Exception {
		dao.create(cus);

		List<Customer> customers = dao.showAllCustomers();
//		assertEquals(1, customers.size());
//		assertEquals(cus.getFirstName(), customers.get(0).getFirstName());
		// searching for uploaded customer in the list of all customers
		boolean customerExists = customers.stream()
			.anyMatch(c -> c.getId().equals(cus.getId()) &&
				c.getFirstName().equals(cus.getFirstName()));

		assertTrue(customerExists, "The created customer should exist in the list of all customers");
	}

	@Test
	void testUpdateNonExistentCustomer() throws Exception {
		// Test: Update a customer that doesn't exist in DB
		Customer nonExistent = new Customer(UUID.randomUUID(), "Ghost", "User", Customer.Gender.D, LocalDate.of(1990, 1, 1));
		assertFalse(dao.updateCustomer(nonExistent));
	}

	@Test
	void testDeleteNonExistentCustomer() throws Exception {
		// Test: Delete a customer that doesn't exist in DB
		UUID nonExistentId = UUID.randomUUID();
		assertFalse(dao.deleteCustomer(nonExistentId));
	}

	@Test
	void testFindByIdNotFound() throws Exception {
		// Test: Find a customer that doesn't exist
		UUID nonExistentId = UUID.randomUUID();
		Customer found = dao.findById(nonExistentId);
		assertNull(found);
	}
}
