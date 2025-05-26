
package acme.features.manager.flight;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flight.Flight;
import acme.entities.leg.Leg;
import acme.features.manager.leg.LegRepository;
import acme.realms.manager.Manager;

@GuiService
public class FlightPublishService extends AbstractGuiService<Manager, Flight> {

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
		Collection<Leg> legs = this.repository.findLegsByFlightId(flight.getId());
		boolean hasLegs = !legs.isEmpty();

		// R1: No dejar publicar si no hay tramos
		super.state(hasLegs, "*", "acme.validation.flight.no-legs.message");

		// R2: No dejar publicar si algún tramo está en borrador
		boolean allLegsPublished = legs.stream().allMatch(leg -> !leg.getDraftMode());
		super.state(allLegsPublished, "*", "acme.validation.flight.legs-not-published.message");

		// R4: casilla de confirmación
		boolean confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");

		//R5: si hay legs publicados y los aeropuertos no son consecutivos, no se puede modificar el atributo selfTransfer
		Collection<Leg> publishedLegs = this.legRepository.findPublishedLegsByFlightId(flight.getId());
		if (!flight.getSelfTransfer() && publishedLegs.size() > 1) {
			boolean airportsAreConsecutive = true;
			Leg previous = null;
			for (Leg current : publishedLegs) {
				if (previous != null)
					if (!previous.getArrivalAirport().equals(current.getDepartureAirport())) {
						airportsAreConsecutive = false;
						break;
					}
				previous = current;
			}
			super.state(airportsAreConsecutive, "*", "acme.validation.flight.selfTransfer-legs-not-consecutive.message");
		}
	}

	@Override
	public void perform(final Flight flight) {
		flight.setDraftMode(false);
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
