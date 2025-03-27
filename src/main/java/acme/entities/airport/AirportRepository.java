
package acme.entities.airport;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AirportRepository {

	@Query("select a from Airport a where a.iataCode = :iataCode")
	Airport findAirportByIataCode(String iataCode);
}
