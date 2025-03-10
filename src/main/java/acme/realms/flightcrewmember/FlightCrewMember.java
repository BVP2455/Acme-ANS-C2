
package acme.realms.flightcrewmember;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.validation.Valid;

import acme.client.components.basis.AbstractRole;
import acme.client.components.datatypes.Money;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoney;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import acme.constraints.ValidFlightCrewMember;
import acme.entities.airline.Airline;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@ValidFlightCrewMember
public class FlightCrewMember extends AbstractRole {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Mandatory Attributes -------------------------------------------------------------

	@Mandatory
	@ValidString(min = 8, max = 9, pattern = "^[A-Z]{2,3}\\d{6}$")
	@Column(unique = true)
	private String				employeeCode;

	@Mandatory
	@ValidString(min = 6, max = 16, pattern = "^\\+?\\d{6,15}$")
	@Automapped
	private String				phoneNumber;

	@Mandatory
	@ValidString(min = 1, max = 255)
	@Automapped
	private String				languageSkills;

	@Mandatory
	@Valid
	@Enumerated(EnumType.STRING)
	@Automapped
	private AvaiabilityStatus	avaiabilityStatus;

	@Mandatory
	@ValidMoney(min = 0, max = 1000000)
	@Automapped
	private Money				salary;

	@Optional
	@ValidNumber(min = 0, max = 120, fraction = 0)
	@Automapped
	private Integer				yearsExperience;

	// Derived attributes -----------------------------------------------------

	// Relationships ----------------------------------------------------------

	//No está aún implementada

	@Mandatory
	@Valid
	@Automapped
	@ManyToOne
	private Airline				airline;

}
