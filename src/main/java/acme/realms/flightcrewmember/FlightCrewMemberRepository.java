
package acme.realms.flightcrewmember;

import org.springframework.data.jpa.repository.Query;

import acme.client.repositories.AbstractRepository;

public interface FlightCrewMemberRepository extends AbstractRepository {

	@Query("select f from FlightCrewMember f where f.employeeCode = :employeeCode")
	FlightCrewMember findFlightCrewMemberByEmployeeCode(String employeeCode);

}
