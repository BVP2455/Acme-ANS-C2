
package acme.features.manager.leg;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircraft.Aircraft;
import acme.entities.airport.Airport;
import acme.entities.leg.Leg;
import acme.entities.leg.LegStatus;
import acme.realms.manager.Manager;

@GuiService
public class LegShowService extends AbstractGuiService<Manager, Leg> {

	@Autowired
	private LegRepository repository;


	@Override
	public void authorise() {
		int legId;
		Leg leg;
		Manager manager;
		boolean authorise = false;

		legId = super.getRequest().getData("id", int.class);
		leg = this.repository.findLegByLegId(legId);
		manager = (Manager) super.getRequest().getPrincipal().getActiveRealm();

		if (manager.getAirline().getId() == leg.getFlight().getAirline().getId())
			authorise = true;
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Leg leg;
		int id;

		id = super.getRequest().getData("id", int.class);
		leg = this.repository.findLegByLegId(id);

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

		//TODO: debería enseñar solo las que pertenecen a la aerolinea del manager
		Collection<Aircraft> aircrafts = this.repository.findAllAircrafts();
		Collection<Airport> airports = this.repository.findAllAirports();

		statusChoices = SelectChoices.from(LegStatus.class, leg.getStatus());
		aircraftChoices = SelectChoices.from(aircrafts, "registrationNumber", leg.getAircraft());
		airports = this.repository.findAllAirports();
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
