
package acme.entities.booking;

import java.beans.Transient;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.datatypes.Money;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import acme.client.helpers.SpringHelper;
import acme.constraints.ValidBooking;
import acme.entities.flight.Flight;
import acme.realms.customer.Customer;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@ValidBooking
public class Booking extends AbstractEntity {
	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Mandatory
	@ValidString(pattern = "^[A-Z0-9]{6,8}$", message = "{acme.validation.locatorCodePattern.message}")
	@Column(unique = true)
	private String				locatorCode;

	@Mandatory
	@ValidMoment(past = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date				purchaseMoment;

	@Mandatory
	@Valid
	@Automapped
	private TravelClass			travelClass;

	@Optional
	@ValidNumber(integer = 4)
	@Automapped
	private Integer				lastCardNibble;

	@Mandatory
	@Automapped
	private boolean				draftMode;

	// Relationships ----------------------------------------------------------

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Customer			customer;

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Flight				flight;

	// Derivated atributes ------------------------------


	@Transient
	public Money getPrice() {
		Money flightCost = this.getFlight().getCost();
		BookingRepository bookingRepository = SpringHelper.getBean(BookingRepository.class);
		Integer numberOfPassengers = bookingRepository.findNumberOfBookingPassengers(this.getId());
		Money price = new Money();
		price.setCurrency(flightCost.getCurrency());
		price.setAmount(flightCost.getAmount() * numberOfPassengers);
		return price;
	}

}
