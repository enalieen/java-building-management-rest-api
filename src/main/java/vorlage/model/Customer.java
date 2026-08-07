package vorlage.model;

import java.time.LocalDate;

import java.util.UUID;

import dev.hv.model.ICustomer;

public class Customer implements ICustomer {

	// Fields representing the customer's data
	private LocalDate birthDate;
	private String firstName, lastName;
	private ICustomer.Gender gender;
	private UUID id;

	/**
	 * No-args constructor.
	 * Required by Jackson (used by Javalin) to deserialize JSON into a Customer object.
	 */
	public Customer() {
	}

	/**
	 * Full constructor with all fields.
	 * Can be used to create a Customer instance manually in code or tests.
	 */
	public Customer(UUID id, String firstName, String lastName, ICustomer.Gender gender, LocalDate birthDate) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.gender = gender;
		this.birthDate = birthDate;
	}

	// Getters and Setters for all fields
	// Jackson uses these methods to populate the object during deserialization

	public LocalDate getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(LocalDate birthDate) {
		this.birthDate = birthDate;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public ICustomer.Gender getGender() {
		return gender;
	}

	public void setGender(ICustomer.Gender gender) {
		this.gender = gender;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}
}
