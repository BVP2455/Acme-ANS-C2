
package acme.entities.booking;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import acme.client.components.basis.AbstractEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class BookingRecord extends AbstractEntity {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@ManyToOne
	@JoinColumn(name = "booking_id", nullable = false)
	private Booking				booking;

	@ManyToOne
	@JoinColumn(name = "passenger_id", nullable = false)
	private Passenger			passenger;
}
