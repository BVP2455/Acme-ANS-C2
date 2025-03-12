
package acme.entities.flights;

import javax.persistence.Entity;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.datatypes.Money;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoney;
import acme.client.components.validation.ValidString;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Flight extends AbstractEntity {

	// Serialisation version ----------------------------

	private static final long	serialVersionUID	= 1L;

	// Mandatory atributes ------------------------------
	@Mandatory
	@ValidString(max = 50)
	@Automapped
	private String				tag;

	@Mandatory
	@Valid
	@Automapped
	private Boolean				selfTransfer;

	@Mandatory
	@ValidMoney(min = 0.0)
	@Automapped
	private Money				cost;

	// Optional atributes -------------------------------
	@Optional
	@ValidString(max = 255)
	@Automapped
	private String				description;

	// Derivated atributes ------------------------------

	//TODO: atributos derivados
	//	private Date getScheduledDeparture() {
	//
	//	}

}
