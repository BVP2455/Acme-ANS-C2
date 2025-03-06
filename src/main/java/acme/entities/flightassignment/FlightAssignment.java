
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

	private static final long	serialVersionUID	= 1L;

	//	@Mandatory
	//	@Valid
	//	@ManyToOne
	//	private FlightCrewMember	flightCrewMember;

	//	@Mandatory
	//	@Valid
	//	@ManyToOne
	//	private Leg leg;

	@Mandatory
	@Valid
	@Automapped
	private FlightCrewDuty		duty;

	@Mandatory
	@ValidMoment(past = true)
	@Automapped
	private Date				moment;

	@Mandatory
	@Valid
	@Automapped
	private CurrentStatus		currentStatus;

	@Optional
	@ValidString(max = 255)
	private String				remarks;

}
