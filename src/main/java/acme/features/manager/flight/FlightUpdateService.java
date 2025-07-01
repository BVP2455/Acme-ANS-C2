
package acme.features.manager.flight;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flight.Flight;
import acme.realms.manager.Manager;

@GuiService
public class FlightUpdateService extends AbstractGuiService<Manager, Flight> {

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

		if (manager.getAirline().getId() == flight.getAirline().getId() && flight.getDraftMode())
			authorise = true;
		super.getResponse().setAuthorised(authorise);
	}

	@Override
	public void load() {
		int flightId;
		Flight flight;

		flightId = super.getRequest().getData("id", int.class);
		flight = (Flight) this.repository.findById(flightId).get();

		super.getBuffer().addData(flight);

		boolean draftMode = flight.getDraftMode();
		super.getResponse().addGlobal("flightDraftMode", draftMode);
	}

	@Override
	public void bind(final Flight flight) {
		super.bindObject(flight, "tag", "selfTransfer", "cost", "description");
	}

	@Override
	public void validate(final Flight flight) {
		boolean isDraftMode;

		isDraftMode = flight.getDraftMode();

		super.state(isDraftMode, "*", "acme.validation.flight.draftMode.updated.message");
	}

	@Override
	public void perform(final Flight flight) {
		this.repository.save(flight);
	}

	@Override
	public void unbind(final Flight flight) {
		Dataset dataset;

		dataset = super.unbindObject(flight, "tag", "selfTransfer", "cost", "description", "draftMode");
		dataset.put("confirmation", false);

		super.getResponse().addData(dataset);
	}

}
