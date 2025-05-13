
package acme.features.manager.leg;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircraft.Aircraft;
import acme.entities.airport.Airport;
import acme.entities.flight.Flight;
import acme.entities.leg.Leg;
import acme.entities.leg.LegStatus;
import acme.features.manager.flight.FlightRepository;
import acme.realms.manager.Manager;

@GuiService
public class LegPublishService extends AbstractGuiService<Manager, Leg> {

	@Autowired
	private LegRepository		repository;

	@Autowired
	private FlightRepository	flightRepository;


	@Override
	public void authorise() {
		int legId;
		Leg leg;
		Manager manager;
		boolean authorise = false;

		legId = super.getRequest().getData("id", int.class);
		leg = (Leg) this.repository.findById(legId).get();
		manager = (Manager) super.getRequest().getPrincipal().getActiveRealm();

		if (manager.getAirline().getId() == leg.getFlight().getAirline().getId() && leg.getDraftMode())
			authorise = true;

		if (authorise) {
			String method;
			int arrivalAirportId, departureAirportId, aircraftId, airlineId;
			Aircraft aircraft;
			Airport arrivalAirport;
			Airport departureAirport;

			method = super.getRequest().getMethod();

			if (method.equals("GET"))
				authorise = true;
			else {
				airlineId = manager.getAirline().getId();
				aircraftId = super.getRequest().getData("aircraft", int.class);
				arrivalAirportId = super.getRequest().getData("airportArrival", int.class);
				departureAirportId = super.getRequest().getData("airportDeparture", int.class);
				aircraft = this.repository.findAircraftByAirlineId(airlineId, aircraftId);
				arrivalAirport = this.repository.findAirportByAirportId(arrivalAirportId);
				departureAirport = this.repository.findAirportByAirportId(departureAirportId);
				authorise = aircraft != null && arrivalAirport != null && departureAirport != null;
			}
		}

		super.getResponse().setAuthorised(authorise);
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		Leg leg = (Leg) this.repository.findById(id).get();
		super.getBuffer().addData(leg);
		Flight flight = (Flight) this.flightRepository.findById(leg.getFlight().getId()).get();
		boolean draftMode = flight.getDraftMode();
		super.getResponse().addGlobal("flightDraftMode", draftMode);
		super.getResponse().addGlobal("legDraftMode", leg.getDraftMode());

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
		Collection<Leg> legs = this.repository.findLegsByFlightId(leg.getFlight().getId());

		boolean status = isDraftMode == true;

		super.state(status, "*", "acme.validation.leg.draftMode.published.message");

		// R1: No dejar publicar si los tramos se solapan
		boolean noOverlap = true;
		Leg previous = null;
		for (Leg current : legs) {
			if (previous != null)
				if (!MomentHelper.isAfter(current.getScheduledDeparture(), previous.getScheduledArrival())) {
					noOverlap = false;
					break;
				}
			previous = current;
		}
		super.state(noOverlap, "*", "acme.validation.flight.legs-overlap.message");

		// R2: aeropuertos deben ser consecutivos
		boolean airportsAreConsecutive = true;
		previous = null;
		for (Leg current : legs) {
			if (previous != null)
				if (!previous.getArrivalAirport().equals(current.getDepartureAirport())) {
					airportsAreConsecutive = false;
					break;
				}
			previous = current;
		}
		super.state(airportsAreConsecutive, "*", "acme.validation.flight.legs-not-consecutive.message");

		boolean confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");
	}

	@Override
	public void perform(final Leg leg) {
		leg.setDraftMode(false);
		this.repository.save(leg);
	}

	@Override
	public void unbind(final Leg leg) {
		Manager manager = (Manager) super.getRequest().getPrincipal().getActiveRealm();
		int airlineId = manager.getAirline().getId();

		Collection<Aircraft> aircrafts = this.repository.findAircraftsByAirlineId(airlineId);
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
