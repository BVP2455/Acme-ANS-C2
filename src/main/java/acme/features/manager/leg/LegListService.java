
package acme.features.manager.leg;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flight.Flight;
import acme.entities.leg.Leg;
import acme.features.manager.flight.FlightRepository;
import acme.realms.manager.Manager;

@GuiService
public class LegListService extends AbstractGuiService<Manager, Leg> {

	@Autowired
	private LegRepository		repository;

	@Autowired
	private FlightRepository	flightRepository;


	@Override
	public void authorise() {
		int flightId;
		Flight flight;
		Manager manager;
		boolean authorise = false;

		flightId = super.getRequest().getData("flightId", int.class);
		flight = this.flightRepository.getFlightById(flightId);
		manager = (Manager) super.getRequest().getPrincipal().getActiveRealm();

		if (manager.getAirline().getId() == flight.getAirline().getId())
			authorise = true;
		super.getResponse().setAuthorised(authorise);
	}

	@Override
	public void load() {
		int flightId = super.getRequest().getData("flightId", int.class);
		Flight flight = (Flight) this.flightRepository.findById(flightId).get();
		Boolean flightDraftMode = flight.getDraftMode();

		Collection<Leg> legs = this.repository.findLegsByFlightId(flightId);
		super.getBuffer().addData(legs);
		super.getResponse().addGlobal("flightDraftMode", flightDraftMode);
		super.getResponse().addGlobal("flightId", flightId);
	}

	@Override
	public void unbind(final Leg leg) {
		Dataset dataset = super.unbindObject(leg, "flightNumber", "scheduledDeparture", "scheduledArrival", "status");
		dataset.put("airportDeparture", leg.getDepartureAirport().getCity());
		dataset.put("airportArrival", leg.getArrivalAirport().getCity());
		dataset.put("aircraft", leg.getAircraft().getModel());
		dataset.put("flightId", super.getRequest().getData("flightId", int.class));
		super.getResponse().addData(dataset);
	}

}
