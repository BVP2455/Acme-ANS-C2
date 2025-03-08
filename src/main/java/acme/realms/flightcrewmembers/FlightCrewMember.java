
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

	//Relationships
	//
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

	//Atributes

	@Mandatory
	@ValidString(min = 8, max = 9, pattern = "^[A-Z]{2-3}\\d{6}$")
	@Column(unique = true)
	private String				employee_code;

	@Mandatory
	@ValidString(min = 6, max = 16, pattern = "^\\+?\\d{6,15}$\")private")
	@Automapped
	String						phone_number;

	@Mandatory
	@ValidString(min = 1, max = 255)
	@Automapped
	private String				language_skills;

	@Mandatory
	@Valid
	@Enumerated(EnumType.STRING)
	@Automapped
	private AvaiabilityStatus	avaiability_status;

	@Mandatory
	@ValidMoney(min = 0, max = 1000000)
	@Automapped
	private Money				salary;

	@Optional
	@ValidNumber(min = 0, max = 120, fraction = 0)
	@Automapped
	private Integer				years_experience;

}
