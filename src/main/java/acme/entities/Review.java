
package acme.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidScore;
import acme.client.components.validation.ValidString;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Review extends AbstractEntity {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Mandatory
	@ValidString(max = 50)
	@Automapped
	String						name;

	@Mandatory
	@ValidMoment(past = true)
	@Automapped
	Date						moment;

	@Mandatory
	@ValidString(max = 50)
	@Automapped
	String						subject;

	@Mandatory
	@ValidString(max = 255)
	@Automapped
	String						text;

	@Optional
	@ValidScore()
	@ValidNumber(min = 0, max = 10)
	@Automapped
	Double						score;

	@Optional
	@Valid
	@Automapped
	Boolean						recommended;
}
