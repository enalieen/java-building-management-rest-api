# Testing Guide

## Test Philosophy
We test everything that matters! Unit tests are your safety net when refactoring and adding features.
All database operations and DAO methods must be tested.

## Test Structure
Tests are located in `src/test/java/` following the same package structure as the main code.

### Current Test Classes

#### JDBCDBConnTest
Tests for database connection handling - 13 test methods covering:
- Connection opening (valid/invalid files)
- Table creation and deletion
- Data truncation
- Connection closing (normal and edge cases)
- Placeholder replacement in connection URLs

#### CustomerDAOTest
Tests for Customer CRUD operations - 7 test methods:
- Create and find customers
- Update existing customers
- Delete customers
- Show all customers
- Edge cases (non-existent customers)

#### ReadingDAOTest
Tests for Reading CRUD operations - 8 test methods:
- Create and find readings
- Delete readings
- Show all readings
- Customer validation (no orphaned readings!)
- Null value handling

## Running Tests

### Run all tests
```sh
mvn clean test
```

### Run specific test class
```sh
mvn test -Dtest=CustomerDAOTest
```

### Run with coverage report
```sh
mvn clean install site -P test
```
Coverage report will be in `target/site/jacoco/index.html`

### Run specific test method
```sh
mvn test -Dtest=CustomerDAOTest#testCreateAndFindCustomer
```

## Test Configuration

### Database Properties
Tests use `src/test/resources/db.properties` for database configuration.
Make sure your test database is running:
```sh
docker-compose up -d
```

### Test Data Isolation
Each test uses `@BeforeEach` to:
1. Connect to test database
2. Drop all tables
3. Recreate tables
4. Create fresh test data

This ensures tests don't interfere with each other!

## Writing Tests

### Basic Test Structure
```java
@Test
void testMethodName_scenario_expectedBehavior() throws Exception {
    // Arrange: Setup test data
    Customer customer = new Customer(...);

    // Act: Execute the method being tested
    boolean result = dao.create(customer);

    // Assert: Verify the result
    assertTrue(result);
}
```

### Common Assertions
```java
// Check for equality
assertEquals(expected, actual);

// Check for null
assertNull(value);
assertNotNull(value);

// Check boolean conditions
assertTrue(condition);
assertFalse(condition);

// Check exceptions
assertThrows(RuntimeException.class, () -> {
    dao.methodThatShouldFail();
});

// Check no exception
assertDoesNotThrow(() -> dao.methodThatShouldWork());
```

## Test Naming Convention
Use descriptive test names that explain:
- What is being tested
- The scenario/input
- Expected behavior

Format: `test<Method>_<scenario>_<expected>`

Examples:
- `testCreateCustomer_validData_returnsTrue()`
- `testFindById_nonExistent_returnsNull()`
- `testDeleteCustomer_alreadyDeleted_returnsFalse()`

## Important Test Cases

### Database Connection Tests
Always test:
- Valid connection (should succeed)
- Invalid properties file (should throw exception)
- Connection closing (normal and edge cases)
- Table operations on closed connections (should throw)

### DAO Tests
Always test:
- Happy path (create, read, update, delete)
- Non-existent entities (should return null or false)
- Null handling
- Foreign key constraints

### Edge Cases We Cover
1. **Double closing**: Closing an already closed connection should not throw
2. **Null connections**: Operations on null connections should be handled
3. **Non-existent entities**: Operations should return false or null, not throw
4. **Orphaned readings**: Reading without customer should throw IllegalArgumentException

## Test Coverage Goals
- **Minimum**: 80% code coverage
- **Target**: 90%+ for critical business logic
- Focus on testing behavior, not just code coverage numbers!

## Mockito Tests (Optional)
We have a commented Mockito version in `CustomerDAOTest.java` that shows how to:
- Mock database connections
- Mock PreparedStatements
- Verify SQL method calls
- Test without actual database

Use Mockito when:
- Testing complex interactions
- Avoiding slow database calls in unit tests
- Isolating the code under test

## CI/CD Integration
Tests run automatically on GitHub Actions:
- On every push to `jUnit` branch
- On pull requests
- Reports are generated and archived

Check `.github/workflows/CI.yml` for configuration.

## Debugging Failed Tests

### Test fails locally
1. Check if Docker database is running: `docker ps`
2. Verify `db.properties` connection details
3. Check if tables exist: `docker exec -it <container> mariadb -u team -p`
4. Enable verbose output: `mvn test -X`

### Test passes locally but fails in CI
1. Check GitHub Actions logs
2. Verify test database credentials match CI config
3. Look for timing issues or race conditions
4. Check if test depends on local environment

## Best Practices

### DO
- Test one thing per test method
- Use descriptive test names
- Clean up test data with `@BeforeEach`
- Test edge cases and error conditions
- Keep tests independent (no order dependency)
- Use assertions that give clear error messages

### DON'T
- Test framework code (like JUnit itself)
- Test getters/setters without logic
- Make tests depend on each other
- Hardcode connection strings (use properties)
- Ignore flaky tests (fix them!)
- Skip testing exceptions and error paths

## Test Data Best Practices

### Use Realistic Data
```java
// Good: Realistic test customer
Customer customer = new Customer(
    UUID.randomUUID(),
    "Mariia",
    "Buashuieva",
    Gender.W,
    LocalDate.of(2000, 2, 16)
);

// Avoid: Meaningless test data
Customer customer = new Customer(
    UUID.randomUUID(),
    "A",
    "B",
    Gender.M,
    LocalDate.now()
);
```

### Reuse Test Data Setup
Use `@BeforeEach` to avoid duplication:
```java
private CustomerDAO dao;
private Customer testCustomer;

@BeforeEach
void setup() {
    // Setup code runs before each test
    dao = new CustomerDAO(conn);
    testCustomer = createTestCustomer();
}
```

## Quick Reference

### JUnit 5 Annotations
- `@Test` - Marks a test method
- `@BeforeEach` - Runs before each test
- `@AfterEach` - Runs after each test
- `@BeforeAll` - Runs once before all tests (static)
- `@AfterAll` - Runs once after all tests (static)

### Maven Test Commands
```sh
mvn test                          # Run all tests
mvn test -Dtest=ClassName         # Run one test class
mvn test -Dtest=Class#method      # Run one test method
mvn clean test                    # Clean and test
mvn test -DskipTests              # Skip tests (don't do this!)
mvn surefire-report:report        # Generate HTML test report
```

### Checking Coverage
After running `mvn clean install site -P test`:
```sh
open target/site/jacoco/index.html    # macOS
xdg-open target/site/jacoco/index.html  # Linux
start target/site/jacoco/index.html   # Windows
```

## Resources
- JUnit 5 Docs: https://junit.org/junit5/docs/current/user-guide/
- Mockito Docs: https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html
- Assertion Guide: https://junit.org/junit5/docs/current/api/org.junit.jupiter.api/org/junit/jupiter/api/Assertions.html

---

Remember: **If it's not tested, it's broken!**
