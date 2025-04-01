
package acme.entities.airline;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidEmail;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidString;
import acme.client.components.validation.ValidUrl;
import acme.constraints.ValidAirline;
import acme.constraints.ValidCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@ValidAirline
public class Airline extends AbstractEntity {

	// Serialisation version -----------------------
	private static final long	serialVersionUID	= 1L;

	//Mandatory atributes
	@Mandatory
	@ValidString(max = 50)
	@Automapped
	private String				name;

	@Mandatory
	@ValidCode
	@Automapped
	private String				code;

	@Mandatory
	@ValidUrl
	@Automapped
	private String				website;

	@Mandatory
	@Valid
	@Automapped
	private AirlineType			type;

	@Mandatory
	@ValidMoment(past = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date				foundationMoment;

	// Optional atributes ------------------
	@Optional
	@ValidEmail
	@Automapped
	private String				email;

	@Optional
	@ValidString(pattern = "^\\+?\\d{6,15}$", message = "{acme.validation.phoneNumber.message}")
	@Automapped
	private String				phoneNumber;

}
