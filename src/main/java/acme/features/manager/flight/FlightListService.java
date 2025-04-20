
package acme.features.manager.flight;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flight.Flight;
import acme.realms.manager.Manager;

@GuiService
public class FlightListService extends AbstractGuiService<Manager, Flight> {

	// Internal state --------------------------------------------------------

	@Autowired
	private FlightRepository repository;

	//AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Collection<Flight> flights;
		Manager manager;
		int airlineId;

		manager = (Manager) super.getRequest().getPrincipal().getActiveRealm();
		airlineId = manager.getAirline().getId();
		flights = this.repository.findFlightsByAirlineId(airlineId);

		super.getBuffer().addData(flights);
	}

	@Override
	public void unbind(final Flight flight) {
		Dataset dataset;

		dataset = super.unbindObject(flight, "tag", "description", "selfTransfer");
		dataset.put("layovers", flight.getNumberLayovers());
		super.addPayload(dataset, flight, "cost");

		super.getResponse().addData(dataset);
	}

}
