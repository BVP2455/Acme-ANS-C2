
package acme.entities.aircraft;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Aircraft extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@ValidString(max = 50)
	@Max(50)
	@Automapped
	private String				model;

	@Mandatory
	@ValidString(max = 50)
	@Column(unique = true)
	private String				registration_number;

	@Mandatory
	@ValidNumber(min = 1, fraction = 0)
	@Min(1)
	@Automapped
	private Integer				capacity;

	@Mandatory
	@ValidNumber(min = 2000, max = 50000)
	@Automapped
	private Double				cargoWeight;

	@Mandatory
	@Valid
	@Enumerated(EnumType.STRING)
	@Automapped
	private Status				status;

	@Optional
	@ValidString(max = 250)
	@Automapped
	String						details;
}
