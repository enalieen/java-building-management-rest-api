package dev.hv.model;

import java.time.LocalDate;

public interface IReading extends IId {

	public static enum KindOfMeter {
		HEIZUNG, STROM, UNBEKANNT, WASSER;
	}

	String getComment();

	void setComment(String comment);

	ICustomer getCustomer();

	void setCustomer(ICustomer customer);

	LocalDate getDateOfReading();

	void setDateOfReading(LocalDate dateOfReading);

	KindOfMeter getKindOfMeter();

	void setKindOfMeter(KindOfMeter kindOfMeter);

	Double getMeterCount();

	void setMeterCount(Double meterCount);

	String getMeterId();

	void setMeterId(String meterId);

	Boolean getSubstitute();

	void setSubstitute(Boolean substitute);

	String printDateOfReading();

}
