
package acme.entities.flightassignment;

import java.util.Date;

import javax.persistence.Entity;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidString;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class FlightAssignment extends AbstractEntity {

	// Serialisation version --------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Mandatory Attributes -------------------------------------------------------

	@Mandatory
	@Valid
	@Automapped
	private FlightCrewDuty		duty;

	@Mandatory
	@ValidMoment(min = "2000/01/01  00:00:00", past = true)
	@Automapped
	private Date				moment;

	@Mandatory
	@Valid
	@Automapped
	private CurrentStatus		currentStatus;

	// Optional Attributes -------------------------------------------------------------

	@Optional
	@ValidString
	private String				remarks;

	// Relationships ----------------------------------------------------------

	//	@Mandatory
	//	@Valid
	//	@ManyToOne
	//	private FlightCrewMember	flightCrewMember;

	//	@Mandatory
	//	@Valid
	//	@ManyToOne
	//	private Leg leg;

}
