
package acme.features.manager.flight;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flight.Flight;
import acme.realms.manager.Manager;

@GuiService
public class FlightShowService extends AbstractGuiService<Manager, Flight> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private FlightRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		int flightId;
		Flight flight;
		Manager manager;
		boolean authorise = false;

		flightId = super.getRequest().getData("id", int.class);
		flight = this.repository.getFlightById(flightId);
		manager = (Manager) super.getRequest().getPrincipal().getActiveRealm();

		if (manager.getAirline().getId() == flight.getAirline().getId())
			authorise = true;
		super.getResponse().setAuthorised(authorise);
	}

	@Override
	public void load() {
		int flightId;
		Flight flight;

		flightId = super.getRequest().getData("id", int.class);
		flight = this.repository.getFlightById(flightId);

		super.getResponse().addGlobal("flightDraftMode", flight.getDraftMode());

		super.getBuffer().addData(flight);
	}

	@Override
	public void validate(final Flight flight) {
		;
	}

	@Override
	public void unbind(final Flight flight) {
		Dataset dataset;

		dataset = super.unbindObject(flight, "tag", "selfTransfer", "cost", "description");
		dataset.put("confirmation", false);

		super.getResponse().addData(dataset);
	}

}
