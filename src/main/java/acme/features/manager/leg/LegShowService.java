
package acme.features.manager.leg;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
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
public class LegShowService extends AbstractGuiService<Manager, Leg> {

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

		if (manager.getAirline().getId() == leg.getFlight().getAirline().getId())
			authorise = true;
		super.getResponse().setAuthorised(authorise);
	}

	@Override
	public void load() {
		Leg leg;
		int id;

		id = super.getRequest().getData("id", int.class);
		leg = (Leg) this.repository.findById(id).get();

		Flight flight = (Flight) this.flightRepository.findById(leg.getFlight().getId()).get();
		boolean draftMode = flight.getDraftMode();
		super.getResponse().addGlobal("flightDraftMode", draftMode);

		super.getBuffer().addData(leg);
	}

	@Override
	public void unbind(final Leg leg) {
		SelectChoices statusChoices;
		SelectChoices aircraftChoices;
		SelectChoices departureChoices;
		SelectChoices arrivalChoices;
		Dataset dataset;
		Manager manager;
		int airlineId;

		manager = (Manager) super.getRequest().getPrincipal().getActiveRealm();
		airlineId = manager.getAirline().getId();

		Collection<Aircraft> aircrafts = this.repository.findAircraftsByAirlineId(airlineId);
		Collection<Airport> airports = this.repository.findAllAirports();

		statusChoices = SelectChoices.from(LegStatus.class, leg.getStatus());
		aircraftChoices = SelectChoices.from(aircrafts, "registrationNumber", leg.getAircraft());
		departureChoices = SelectChoices.from(airports, "name", leg.getDepartureAirport());
		arrivalChoices = SelectChoices.from(airports, "name", leg.getArrivalAirport());

		dataset = super.unbindObject(leg, "flightNumber", "scheduledDeparture", "scheduledArrival", "status");
		dataset.put("statuses", statusChoices);
		dataset.put("aircraft", aircraftChoices.getSelected().getKey());
		dataset.put("aircrafts", aircraftChoices);
		dataset.put("airportDeparture", departureChoices.getSelected().getKey());
		dataset.put("airportDepartures", departureChoices);
		dataset.put("airportArrival", arrivalChoices.getSelected().getKey());
		dataset.put("airportArrivals", arrivalChoices);

		super.getResponse().addData(dataset);
	}

}
