
package acme.features.manager.leg;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
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
public class LegPublishService extends AbstractGuiService<Manager, Leg> {

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

		if (manager.getAirline().getId() == leg.getFlight().getAirline().getId() && leg.getDraftMode())
			authorise = true;

		if (authorise) {
			int arrivalAirportId, departureAirportId, aircraftId, airlineId;
			Aircraft aircraft;
			Airport arrivalAirport;
			Airport departureAirport;

			airlineId = manager.getAirline().getId();
			aircraftId = super.getRequest().getData("aircraft", int.class);
			arrivalAirportId = super.getRequest().getData("airportArrival", int.class);
			departureAirportId = super.getRequest().getData("airportDeparture", int.class);
			aircraft = this.repository.findAircraftByAirlineId(airlineId, aircraftId);
			arrivalAirport = this.repository.findAirportByAirportId(arrivalAirportId);
			departureAirport = this.repository.findAirportByAirportId(departureAirportId);
			authorise = (aircraftId == 0 || aircraft != null) && (arrivalAirportId == 0 || arrivalAirport != null) && (departureAirportId == 0 || departureAirport != null);
		}

		super.getResponse().setAuthorised(authorise);
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		Leg leg = (Leg) this.repository.findById(id).get();
		super.getBuffer().addData(leg);
		Flight flight = (Flight) this.flightRepository.findById(leg.getFlight().getId()).get();
		boolean draftMode = flight.getDraftMode();
		super.getResponse().addGlobal("flightDraftMode", draftMode);
		super.getResponse().addGlobal("legDraftMode", leg.getDraftMode());

	}

	@Override
	public void bind(final Leg leg) {
		int aircraftId = super.getRequest().getData("aircraft", int.class);
		int departureAirportId = super.getRequest().getData("airportDeparture", int.class);
		int arrivalAirportId = super.getRequest().getData("airportArrival", int.class);

		Aircraft aircraft = this.repository.findAircraftByAircraftId(aircraftId);
		Airport departure = this.repository.findAirportByAirportId(departureAirportId);
		Airport arrival = this.repository.findAirportByAirportId(arrivalAirportId);

		leg.setAircraft(aircraft);
		leg.setDepartureAirport(departure);
		leg.setArrivalAirport(arrival);

		super.bindObject(leg, "flightNumber", "scheduledDeparture", "scheduledArrival", "status");
	}

	@Override
	public void validate(final Leg leg) {
		Collection<Leg> legs = this.repository.findPublishedLegsByFlightId(leg.getFlight().getId());
		legs = legs.stream().filter(l -> l.getId() != leg.getId()).collect(Collectors.toList());
		legs.add(leg);

		// R1: momento de salida y de llegada deben se posterior a la fecha actual
		if (leg.getScheduledDeparture() != null) {
			boolean futureDepartureDate = MomentHelper.isFuture(leg.getScheduledDeparture());
			super.state(futureDepartureDate, "scheduledDeparture", "acme.validation.leg.scheduled-departure-not-future.message");
		}
		if (leg.getScheduledArrival() != null) {
			boolean futureArrivalDate = MomentHelper.isFuture(leg.getScheduledArrival());
			super.state(futureArrivalDate, "scheduledArrival", "acme.validation.leg.scheduled-arrival-not-future.message");
		}

		//R4: no puede existir otro leg con el mismo flight number
		String flightNumber = leg.getFlightNumber();
		boolean isUnique = !this.repository.existsByFlightNumberAndIdNot(flightNumber, leg.getId());
		super.state(isUnique, "flightNumber", "acme.validation.leg.duplicated-code.message");

		//R3: no puede ser el mismo aeropuerto de llegada que el de salida
		if (leg.getArrivalAirport() != null && leg.getDepartureAirport() != null) {
			boolean isDifferent = !leg.getArrivalAirport().equals(leg.getDepartureAirport());
			super.state(isDifferent, "airportArrival", "acme.validation.leg.same-airports.message");
		}

		//R7: los aeropuertos de llegada y salida no pueden ser nulos
		if (leg.getDepartureAirport() == null)
			super.state(false, "airportDeparture", "acme.validation.leg.departure-airport-not-null.message");
		if (leg.getArrivalAirport() == null)
			super.state(false, "airportArrival", "acme.validation.leg.arrival-airport-not-null.message");

		//R6: requisito de confirmacion
		boolean confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");

		//R9: No dejar publicar si los tramos se solapan
		if (legs.size() > 1) {
			boolean noOverlap = true;
			Leg previous = null;
			for (Leg current : legs) {
				if (previous != null)
					if (!MomentHelper.isAfterOrEqual(current.getScheduledDeparture(), previous.getScheduledArrival())) {
						noOverlap = false;
						break;
					}
				previous = current;
			}
			super.state(noOverlap, "*", "acme.validation.flight.legs-overlap.message");
		}

		//R10: aeropuertos deben ser consecutivos si el vuelo no está en trasbordo
		if (!leg.getFlight().getSelfTransfer() && legs.size() > 1) {
			boolean airportsAreConsecutive = true;
			Leg previous = null;
			for (Leg current : legs) {
				if (previous != null)
					if (!previous.getArrivalAirport().equals(current.getDepartureAirport())) {
						airportsAreConsecutive = false;
						break;
					}
				previous = current;
			}
			super.state(airportsAreConsecutive, "*", "acme.validation.flight.legs-not-consecutive.message");
		}

	}

	@Override
	public void perform(final Leg leg) {
		leg.setDraftMode(false);
		this.repository.save(leg);
	}

	@Override
	public void unbind(final Leg leg) {
		Manager manager = (Manager) super.getRequest().getPrincipal().getActiveRealm();
		int airlineId = manager.getAirline().getId();

		Collection<Aircraft> aircrafts = this.repository.findAircraftsByAirlineId(airlineId);
		Collection<Airport> airports = this.repository.findAllAirports();

		SelectChoices statusChoices = SelectChoices.from(LegStatus.class, leg.getStatus());
		SelectChoices aircraftChoices = SelectChoices.from(aircrafts, "registrationNumber", leg.getAircraft());
		SelectChoices departureChoices = SelectChoices.from(airports, "iataCode", leg.getDepartureAirport());
		SelectChoices arrivalChoices = SelectChoices.from(airports, "iataCode", leg.getArrivalAirport());

		Dataset dataset = super.unbindObject(leg, "flightNumber", "scheduledDeparture", "scheduledArrival", "status");

		dataset.put("statuses", statusChoices);
		dataset.put("aircraft", aircraftChoices.getSelected().getKey());
		dataset.put("aircrafts", aircraftChoices);
		dataset.put("airportDeparture", departureChoices.getSelected().getKey());
		dataset.put("airportDepartures", departureChoices);
		dataset.put("airportArrival", arrivalChoices.getSelected().getKey());
		dataset.put("airportArrivals", arrivalChoices);
		dataset.put("flightId", leg.getFlight().getId());

		super.getResponse().addData(dataset);
	}
}
