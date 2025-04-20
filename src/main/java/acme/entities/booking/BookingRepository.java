
package acme.entities.booking;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface BookingRepository extends AbstractRepository {

	@Query("select b from Booking b where b.locatorCode = :locatorCode")
	Booking findBookingByLocatorCode(String locatorCode);

	@Query("SELECT COUNT(br) FROM BookingRecord br WHERE br.booking.id = :bookingId")
	Integer findNumberOfBookingPassengers(Integer bookingId);

	@Query("select b from Booking b where b.flight.id = :flightId")
	Collection<Booking> findBookingByFlightId(int flightId);
}
