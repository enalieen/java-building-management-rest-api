package vorlage.model;

import java.time.LocalDate;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.hv.model.IReading;
import dev.hv.model.ICustomer;

public class Reading implements IReading {

	private Customer customer;
	private LocalDate dateOfReading;
	private UUID id;
	private IReading.KindOfMeter kindOfMeter;
	private Double meterCount;
	private String meterId;
	private String comment;
	private Boolean substitute;

	// Full constructor
	public Reading(UUID id, Customer customer, LocalDate dateOfReading,
				   IReading.KindOfMeter kindOfMeter, Double meterCount,
				   String meterId, String comment, Boolean substitute) {
		this.id = id;
		this.customer = customer;
		this.dateOfReading = dateOfReading;
		this.kindOfMeter = kindOfMeter;
		this.meterCount = meterCount;
		this.kindOfMeter = kindOfMeter;
		this.meterCount = meterCount;
		this.meterId = meterId;
		this.comment = comment;
		this.substitute = substitute;
	}

	// Default constructor required by Jackson for JSON deserialization
	public Reading() {}

	// Getters
	public UUID getId() {
		return id;
	}

	@Override
	public ICustomer getCustomer() {
		return customer;
	}

	public LocalDate getDateOfReading() {
		return dateOfReading;
	}

	public IReading.KindOfMeter getKindOfMeter() {
		return kindOfMeter;
	}

	public Double getMeterCount() {
		return meterCount;
	}

	public String getMeterId() {
		return meterId;
	}

	public String getComment() {
		return comment;
	}

	public Boolean getSubstitute() {
		return substitute;
	}

	// Setters
	public void setId(UUID id) {
		this.id = id;
	}

	@Override
	public void setCustomer(ICustomer customer) {
		if (customer != null && !(customer instanceof Customer)) {
			throw new IllegalArgumentException("Only Customer instances supported");
		}
		this.customer = (Customer) customer;
	}

	// Jackson uses this method to deserialize the concrete Customer type from JSON
	@JsonProperty("customer")
	public void setCustomerFromJson(Customer customer) {
		this.customer = customer;
	}

	public void setDateOfReading(LocalDate dateOfReading) {
		this.dateOfReading = dateOfReading;
	}

	public void setKindOfMeter(IReading.KindOfMeter kindOfMeter) {
		this.kindOfMeter = kindOfMeter;
	}

	public void setMeterCount(Double meterCount) {
		this.meterCount = meterCount;
	}

	public void setMeterId(String meterId) {
		this.meterId = meterId;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public void setSubstitute(Boolean substitute) {
		this.substitute = substitute;
	}

	@Override
	public String printDateOfReading() {
		return dateOfReading != null ? dateOfReading.toString() : null;
	}
}
