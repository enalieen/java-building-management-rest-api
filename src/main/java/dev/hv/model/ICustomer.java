package dev.hv.model;

import java.time.LocalDate;

public interface ICustomer extends IId {

	public static enum Gender {
		D, // divers
		M, // männlich
		U, // unbekannt
		W; // weiblich
	}

	LocalDate getBirthDate();

	void setBirthDate(LocalDate birthDate);

	String getFirstName();

	void setFirstName(String firstName);

	Gender getGender();

	void setGender(Gender gender);

	String getLastName();

	void setLastName(String lastName);

}
