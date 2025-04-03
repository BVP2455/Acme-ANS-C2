
package acme.features.customer.passenger;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.Passenger;
import acme.entities.booking.Booking;

@Repository
public interface CustomerPassengerRepository extends AbstractRepository {

	@Query("select p from Passenger p where p.id = :id")
	Passenger findPassengerById(int id);

	@Query("select p from Passenger p where p.customer.id = :customerId")
	Collection<Passenger> findAllPassengersOfCustomer(int customerId);

	@Query("select br.passenger from BookingRecord br where br.booking.id=:bookingId")
	Collection<Passenger> findPassengersByBookingId(int bookingId);

	@Query("select b from Booking b where b.id=:bookingId")
	Booking findBookingById(int bookingId);
}
