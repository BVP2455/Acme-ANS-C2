
package acme.features.manager.leg;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.leg.Leg;
import acme.features.manager.flight.FlightRepository;
import acme.realms.manager.Manager;

@GuiService
public class LegListService extends AbstractGuiService<Manager, Leg> {

	// Internal state --------------------------------------------------------

	@Autowired
	private LegRepository		legRepository;

	@Autowired
	private FlightRepository	flightRepository;

	//AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Collection<Leg> legs;
		int flightId;

		flightId = super.getResponse().getData("id", int.class);
		legs = this.legRepository.findLegsByFlightId(flightId);

		super.getBuffer().addData(legs);
	}

	@Override
	public void unbind(final Leg leg) {
		Dataset dataset;

		dataset = super.unbindObject(leg, "flightNumber", "scheduledDeparture", "scheduledArrival", "status");
		dataset.put("departureAirport", leg.getDepartureAirport().getCity());
		dataset.put("arrivalAirport", leg.getArrivalAirport().getCity());
		dataset.put("aircraft", leg.getAircraft().getModel());

		super.getResponse().addData(dataset);

	}

}
