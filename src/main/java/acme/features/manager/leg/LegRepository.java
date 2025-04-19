
package acme.features.manager.leg;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.aircraft.Aircraft;
import acme.entities.airport.Airport;
import acme.entities.leg.Leg;

@Repository
public interface LegRepository extends AbstractRepository {

	@Query("SELECT l FROM Leg l WHERE l.flight.id = :flightId order by l.scheduledDeparture")
	Collection<Leg> findLegsByFlightId(int flightId);

	@Query("select l from Leg l where l.flight.id = :flightId order by l.scheduledDeparture")
	List<Leg> findLegsByFlightOrderedByDeparture(Integer flightId);

	@Query("select count(l) from Leg l where l.flight.id = :flightId")
	Integer countNumberOfLegsOfFlight(Integer flightId);

	@Query("select a from Airport a")
	Collection<Airport> findAllAirports();

	@Query("select a from Aircraft a where a.airline.id = :airlineId")
	Collection<Aircraft> findAircraftsByAirlineId(int airlineId);

	@Query("select l from Leg l where l.id = :legId")
	Leg findLegByLegId(int legId);

	@Query("select a from Aircraft a")
	public Collection<Aircraft> findAllAircrafts();

}
