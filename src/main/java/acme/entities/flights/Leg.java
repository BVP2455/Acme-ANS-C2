
package acme.entities.flights;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import acme.entities.aircraft.Aircraft;
import acme.entities.airport.Airport;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Leg extends AbstractEntity {

	//Serialisation version -------------------------
	private static final long	serialVersionUID	= 1L;

	//Mandatory atributes ---------------------------
	@Mandatory
	@ValidString(pattern = "^[A-Z]{3}[0-9]{4}$")
	@Column(unique = true)
	@Automapped
	private String				flightNumber;

	@Mandatory
	@ValidMoment
	@Temporal(TemporalType.TIMESTAMP)
	@Automapped
	private Date				scheduledDeparture;

	@Mandatory
	@ValidMoment
	@Temporal(TemporalType.TIMESTAMP)
	@Automapped
	private Date				scheduledArrival;

	@Mandatory
	@ValidNumber
	@Automapped
	private Integer				duration;

	@Mandatory
	@Automapped
	private LegStatus			status;

	@Mandatory
	private Airport				departureAirport;

	@Mandatory
	private Airport				arrivalAirport;

	@Mandatory
	private Aircraft			aircraft;

}
