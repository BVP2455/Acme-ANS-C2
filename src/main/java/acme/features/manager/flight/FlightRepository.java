
package acme.features.manager.flight;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.flight.Flight;

@Repository
public interface FlightRepository extends AbstractRepository {

	@Query("SELECT f FROM Flight f WHERE f.airline.id = :airlineId")
	Collection<Flight> findFlightsByAirlineId(int airlineId);

	@Query("SELECT f FROM Flight f WHERE f.id = :flightId")
	Flight getFlightById(int flightId);

}
