
package acme.features.flightcrewmember.flightassignment;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.flightassignment.FlightAssignment;
import acme.entities.legs.Leg;
import acme.realms.flightcrewmember.FlightCrewMember;

@Repository
public interface FlightAssignmentRepository extends AbstractRepository {

	@Query("select fa from FlightAssignment fa where fa.leg.scheduledArrival < CURRENT_TIMESTAMP")
	Collection<FlightAssignment> findCompletedFlightAssignments();

	@Query("select fa from FlightAssignment fa where fa.leg.scheduledDeparture > CURRENT_TIMESTAMP")
	Collection<FlightAssignment> findPlannedFlightAssignments();

	@Query("SELECT fa FROM FlightAssignment fa WHERE fa.flightCrewMember.id = :flightCrewMemberId AND fa.leg.scheduledArrival < CURRENT_TIMESTAMP")
	Collection<FlightAssignment> findCompletedFlightAssignmentsByMemberId(final int flightCrewMemberId);

	@Query("SELECT fa FROM FlightAssignment fa WHERE fa.flightCrewMember.id = :flightCrewMemberId AND fa.leg.scheduledDeparture > CURRENT_TIMESTAMP")
	Collection<FlightAssignment> findPlannedFlightAssignmentsByMemberId(final int flightCrewMemberId);

	@Query("SELECT fa FROM FlightAssignment fa WHERE fa.id = :id")
	FlightAssignment findFlightAssignmentById(int id);

	@Query("SELECT l FROM Leg l")
	Collection<Leg> findAllLegs();

	@Query("SELECT fcm FROM FlightCrewMember fcm")
	Collection<FlightCrewMember> findAllFlightCrewMembers();

	@Query("SELECT l FROM Leg l WHERE l.id = :legId")
	Leg findLegById(int legId);

	@Query("SELECT fcm FROM FlightCrewMember fcm WHERE fcm.id = :flightCrewMemberId")
	FlightCrewMember findFlightCrewMemberById(int flightCrewMemberId);

	@Query("SELECT fa.leg FROM FlightAssignment fa WHERE fa.flightCrewMember.id = :flightCrewMemberId ORDER BY fa.leg.scheduledDeparture ASC")
	List<Leg> findLegsByMemberId(int flightCrewMemberId);

}
