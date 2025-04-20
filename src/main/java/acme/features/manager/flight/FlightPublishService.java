
package acme.features.manager.flight;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flight.Flight;
import acme.entities.leg.Leg;
import acme.realms.manager.Manager;

@GuiService
public class FlightPublishService extends AbstractGuiService<Manager, Flight> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private FlightRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		int flightId;
		Flight flight;

		flightId = super.getRequest().getData("id", int.class);
		flight = (Flight) this.repository.findById(flightId).get();

		super.getBuffer().addData(flight);
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

		// R2: No dejar publicar si algún tramo está en draftMode
		boolean allLegsPublished = legs.stream().allMatch(leg -> !leg.getDraftMode());
		super.state(allLegsPublished, "*", "acme.validation.flight.legs-not-published.message");

		// R3: No dejar publicar si los tramos se solapan
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

		// R4: Los aeropuertos deben ser consecutivos
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

		// R5: El vuelo solo puede publicarse si está en draftMode (ajusta si tu lógica es diferente)
		boolean isDraftMode = flight.getDraftMode();
		boolean status = isDraftMode == true;
		super.state(status, "*", "acme.validation.flight.draftMode.published.message");

		// R6: Confirmación del usuario
		boolean confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");
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
