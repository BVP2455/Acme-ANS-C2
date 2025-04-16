
package acme.features.administrator.airline;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.airline.Airline;

@Repository
public interface AirlineRepository extends AbstractRepository {

	@Query("SELECT a FROM Airline a WHERE a.id = :id")
	Airline findAirlineById(int id);

	@Query("SELECT a FROM Airline a")
	Collection<Airline> findAllAirlines();

	@Query("SELECT COUNT(a) > 0 FROM Airline a WHERE a.iataCode = :iataCode")
	boolean existsByIataCode(String iataCode);

	@Query("SELECT COUNT(a) > 0 FROM Airline a WHERE a.iataCode = :iataCode AND a.id <> :id")
	boolean existsByIataCodeAndIdNot(String iataCode, int id);

}
