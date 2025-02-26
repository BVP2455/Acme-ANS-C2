
package acme.realms.crewmembers;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.datatypes.Money;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoney;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class FlightCrewMembers extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@ValidString(pattern = "^[A-Z]{2-3}\\d{6}$")
	@Column(unique = true)
	private String				employee_code;

	@Mandatory
	@ValidString(pattern = "^\\+?\\d{6,15}$\")private")
	@Automapped
	String						phone_number;

	@Mandatory
	@ValidString(max = 255)
	@Automapped
	private String				language_skills;

	@Mandatory
	@Valid
	@Enumerated(EnumType.STRING)
	@Automapped
	private AvaiabilityStatus	avaiability_status;

	/*
	 * @Mandatory
	 * 
	 * @Valid
	 * 
	 * @Automapped
	 * 
	 * @ManyToOne
	 * private Airline airline;
	 */
	@Mandatory
	@ValidMoney
	@Automapped
	private Money				salary;

	@Optional
	@ValidNumber(fraction = 0)
	@Automapped
	private Integer				years_experience;

}
