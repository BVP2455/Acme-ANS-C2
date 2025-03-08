
package acme.realms;

import java.util.Random;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Transient;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Customer extends AbstractEntity {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Mandatory
	@ValidString(pattern = "^[A-Z]{2-3}\\d{6}$")
	@Column(unique = true)
	String						identifier;


	@Transient
	public String generateIdentifier(final String firstName, final String lastName) {
		// Obtener las iniciales (máximo 3 letras, en mayúsculas)
		String initials = (firstName.substring(0, 1) + lastName.substring(0, 2)).toUpperCase();

		// Generar 6 dígitos aleatorios
		int randomNumber = new Random().nextInt(1000000); // Número entre 0 y 999999
		String digits = String.format("%06d", randomNumber); // Asegurar 6 dígitos

		return initials + digits;
	}


	@Mandatory
	@ValidString(pattern = "^[A-Z0-9]{6,8}$")
	@Automapped
	String	phoneNumber;

	@Mandatory
	@ValidString(max = 255)
	@Automapped
	String	physicalAddress;

	@Mandatory
	@ValidString(max = 50)
	@Automapped
	String	city;

	@Mandatory
	@ValidString(max = 50)
	@Automapped
	String	country;

	@Optional
	@ValidNumber(max = 500000)
	@Automapped
	Integer	earnedPoints;
}
