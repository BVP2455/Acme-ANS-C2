
package acme.features.manager.leg;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircraft.Aircraft;
import acme.entities.airport.Airport;
import acme.entities.claim.Claim;
import acme.entities.flight.Flight;
import acme.entities.flightassignment.FlightAssignment;
import acme.entities.leg.Leg;
import acme.entities.leg.LegStatus;
import acme.entities.trackingLog.TrackingLog;
import acme.entities.trackingLog.TrackingLogRepository;
import acme.features.assistanceAgent.claim.ClaimRepository;
import acme.features.flightcrewmember.flightassignment.FlightAssignmentRepository;
import acme.features.manager.flight.FlightRepository;
import acme.realms.manager.Manager;

@GuiService
public class LegDeleteService extends AbstractGuiService<Manager, Leg> {

	@Autowired
	private LegRepository				repository;

	@Autowired
	private FlightRepository			flightRepository;

	@Autowired
	private ClaimRepository				claimRepository;

	@Autowired
	private FlightAssignmentRepository	flightAssignmentRepository;

	@Autowired
	private TrackingLogRepository		trackingLogRepository;


	@Override
	public void authorise() {
		int legId = super.getRequest().getData("id", int.class);
		Leg leg = (Leg) this.repository.findById(legId).get();
		Manager manager = (Manager) super.getRequest().getPrincipal().getActiveRealm();

		boolean authorised = manager.getAirline().getId() == leg.getFlight().getAirline().getId();

		super.getResponse().setAuthorised(authorised);
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		Leg leg = (Leg) this.repository.findById(id).get();
		super.getBuffer().addData(leg);
		Flight flight = (Flight) this.flightRepository.findById(leg.getFlight().getId()).get();
		boolean draftMode = flight.getDraftMode();
		super.getResponse().addGlobal("flightDraftMode", draftMode);
	}

	@Override
	public void bind(final Leg leg) {
		int aircraftId = super.getRequest().getData("aircraft", int.class);
		int departureAirportId = super.getRequest().getData("airportDeparture", int.class);
		int arrivalAirportId = super.getRequest().getData("airportArrival", int.class);

		Aircraft aircraft = this.repository.findAircraftByAircraftId(aircraftId);
		Airport departure = this.repository.findAirportByAirportId(departureAirportId);
		Airport arrival = this.repository.findAirportByAirportId(arrivalAirportId);

		leg.setAircraft(aircraft);
		leg.setDepartureAirport(departure);
		leg.setArrivalAirport(arrival);

		super.bindObject(leg, "flightNumber", "scheduledDeparture", "scheduledArrival", "status");
	}

	@Override
	public void validate(final Leg leg) {
		boolean isDraftMode = leg.getDraftMode();

		boolean status = isDraftMode == true;

		super.state(status, "*", "acme.validation.leg.draftMode.deleted.message");
	}

	@Override
	public void perform(final Leg leg) {
		Collection<Claim> claims = this.claimRepository.findClaimsByLegId(leg.getId());
		for (Claim claim : claims) {
			Collection<TrackingLog> trackingLogs = this.trackingLogRepository.findTrackingLogsByClaimId(claim.getId());
			this.trackingLogRepository.deleteAll(trackingLogs);
		}
		Collection<FlightAssignment> flightAssignments = this.flightAssignmentRepository.findFlightAssignmentByLegId(leg.getId());
		this.claimRepository.deleteAll(claims);
		this.flightAssignmentRepository.deleteAll(flightAssignments);

		this.repository.delete(leg);
	}

	@Override
	public void unbind(final Leg leg) {
		Manager manager = (Manager) super.getRequest().getPrincipal().getActiveRealm();
		int airlineId = manager.getAirline().getId();

		Collection<Aircraft> aircrafts = this.repository.findAircraftsByAirlineId(airlineId); // Podrías filtrar por airlineId
		Collection<Airport> airports = this.repository.findAllAirports();

		SelectChoices statusChoices = SelectChoices.from(LegStatus.class, leg.getStatus());
		SelectChoices aircraftChoices = SelectChoices.from(aircrafts, "registrationNumber", leg.getAircraft());
		SelectChoices departureChoices = SelectChoices.from(airports, "name", leg.getDepartureAirport());
		SelectChoices arrivalChoices = SelectChoices.from(airports, "name", leg.getArrivalAirport());

		Dataset dataset = super.unbindObject(leg, "flightNumber", "scheduledDeparture", "scheduledArrival", "status");

		dataset.put("statuses", statusChoices);
		dataset.put("aircraft", aircraftChoices.getSelected().getKey());
		dataset.put("aircrafts", aircraftChoices);
		dataset.put("airportDeparture", departureChoices.getSelected().getKey());
		dataset.put("airportDepartures", departureChoices);
		dataset.put("airportArrival", arrivalChoices.getSelected().getKey());
		dataset.put("airportArrivals", arrivalChoices);
		dataset.put("flightId", leg.getFlight().getId());

		super.getResponse().addData(dataset);
	}
}
