
package acme.entities;

import java.util.Date;

import javax.persistence.Entity;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidEmail;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidString;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Passenger extends AbstractEntity {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Mandatory
	@ValidString(min = 1, max = 255)
	@Automapped
	String						name;

	@Mandatory
	@ValidEmail()
	@ValidString(min = 1, max = 255)
	@Automapped
	String						email;

	@Mandatory
	@ValidString(pattern = "^[A-Z0-9]{6,9}$")
	@Automapped
	String						passportNumber;

	@Mandatory
	@ValidMoment(past = true)
	@Automapped
	Date						dateOfBirth;

	@Optional
	@ValidString(max = 50)
	@Automapped
	String						specialNeeds;

}
