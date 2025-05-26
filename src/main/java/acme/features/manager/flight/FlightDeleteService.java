
package acme.features.manager.flight;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flight.Flight;
import acme.entities.leg.Leg;
import acme.features.manager.leg.LegRepository;
import acme.realms.manager.Manager;

@GuiService
public class FlightDeleteService extends AbstractGuiService<Manager, Flight> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private FlightRepository	repository;

	@Autowired
	private LegRepository		legRepository;

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
		boolean isDraftMode = flight.getDraftMode();

		boolean status = isDraftMode == true;

		super.state(status, "*", "acme.validation.flight.draftMode.deleted.message");
	}

	@Override
	public void perform(final Flight flight) {

		Collection<Leg> legs = this.legRepository.findLegsByFlightId(flight.getId());

		this.legRepository.deleteAll(legs);

		this.repository.delete(flight);
	}

}
