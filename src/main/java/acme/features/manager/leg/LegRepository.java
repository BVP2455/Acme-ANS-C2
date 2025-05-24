
package acme.features.manager.leg;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.aircraft.Aircraft;
import acme.entities.airport.Airport;
import acme.entities.leg.Leg;
import acme.entities.leg.LegStatus;

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

	@Query("select a from Aircraft a where a.id = :aircraftId")
	Aircraft findAircraftByAircraftId(int aircraftId);

	@Query("select a from Airport a where a.id = :airportId")
	Airport findAirportByAirportId(int airportId);

	@Query("SELECT COUNT(l) > 0 FROM Leg l WHERE l.flightNumber = :flightNumber")
	boolean existsByFlightNumber(String flightNumber);

	@Query("SELECT COUNT(l) > 0 FROM Leg l WHERE l.flightNumber = :flightNumber AND l.id <> :id")
	boolean existsByFlightNumberAndIdNot(String flightNumber, int id);

	@Query("SELECT a.id FROM Airport a")
	List<Integer> getAllAirportIds();

	@Query("SELECT a.id FROM Aircraft a")
	List<Integer> getAllAircraftIds();

	@Query("SELECT a FROM Aircraft a WHERE a.id = :aircraftId AND a.airline.id = :airlineId")
	Aircraft findAircraftByAirlineId(int airlineId, int aircraftId);

	@Query("SELECT l.status FROM Leg l WHERE l.status = :status")
	LegStatus findLegStatusByStatusValue(String status);

	@Query("SELECT l FROM Leg l WHERE l.draftMode = false AND l.flight.id = :flightId ORDER BY l.scheduledDeparture ASC")
	Collection<Leg> findPublishedLegsByFlightId(int flightId);

}
