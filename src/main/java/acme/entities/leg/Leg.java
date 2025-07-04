
package acme.entities.leg;

import java.time.Duration;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidString;
import acme.client.helpers.MomentHelper;
import acme.constraints.ValidLeg;
import acme.entities.aircraft.Aircraft;
import acme.entities.airport.Airport;
import acme.entities.flight.Flight;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@ValidLeg
@Table(indexes = {
	@Index(columnList = "draftMode"), @Index(columnList = "draftMode, scheduledDeparture, scheduledArrival"), @Index(columnList = "draftMode, id"), @Index(columnList = "flight_id, scheduledDeparture"), @Index(columnList = "flight_id"),
	@Index(columnList = "flightNumber, id"), @Index(columnList = "status"), @Index(columnList = "draftMode, flight_id, scheduledDeparture")
})
public class Leg extends AbstractEntity {

	//Serialisation version -------------------------
	private static final long	serialVersionUID	= 1L;

	//Mandatory atributes ---------------------------
	@Mandatory
	@ValidString(pattern = "^[A-Z]{3}[0-9]{4}$", message = "Invalid flight number")
	@Column(unique = true)
	private String				flightNumber;

	@Mandatory
	@ValidMoment
	@Temporal(TemporalType.TIMESTAMP)
	private Date				scheduledDeparture;

	@Mandatory
	@ValidMoment
	@Temporal(TemporalType.TIMESTAMP)
	private Date				scheduledArrival;


	@Transient
	private Double getDuration() {
		Duration duration = MomentHelper.computeDuration(this.getScheduledDeparture(), this.getScheduledArrival());

		return duration.getSeconds() / 60.;
	}


	@Mandatory
	@Valid
	@Automapped
	private LegStatus	status;

	@Mandatory
	@Valid
	@Automapped
	private Boolean		draftMode;

	// Relationships ----------------------------------------------------------

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Flight		flight;

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Airport		departureAirport;

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Airport		arrivalAirport;

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Aircraft	aircraft;

}
