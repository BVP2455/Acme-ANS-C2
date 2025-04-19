
package acme.features.manager.leg;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.leg.Leg;

@Repository
public interface LegRepository extends AbstractRepository {

	@Query("SELECT l FROM Leg WHERE l.flight.id = :flightId")
	Collection<Leg> findLegsByFlightId(int flightId);

	@Query("select l from Leg l where l.flight.id = :flightId order by l.scheduledDeparture")
	List<Leg> findLegsByFlightOrderedByDeparture(Integer flightId);

	@Query("select count(l) from Leg l where l.flight.id = :flightId")
	Integer countNumberOfLegsOfFlight(Integer flightId);

}
