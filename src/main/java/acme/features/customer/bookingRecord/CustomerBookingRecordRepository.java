
package acme.features.customer.bookingRecord;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.Passenger;
import acme.entities.booking.Booking;
import acme.entities.booking.BookingRecord;

@Repository
public interface CustomerBookingRecordRepository extends AbstractRepository {

	@Query("select b from Booking b where b.id=:bookingId")
	Booking findBookingById(int bookingId);

	@Query("select p from Passenger p where p.customer.id=:customerId")
	Collection<Passenger> findAllPassengersOfCustomer(int customerId);

	@Query("select br.passenger from BookingRecord br where br.booking.id=:bookingId")
	Collection<Passenger> findPassengersOfBooking(int bookingId);

	@Query("select br from BookingRecord br where br.booking.id = :bookingId")
	Collection<BookingRecord> findBookingRecordsByBookingId(int bookingId);

}
