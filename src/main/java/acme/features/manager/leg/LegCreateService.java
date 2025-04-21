
package acme.features.manager.leg;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.helpers.StringHelper;
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
public class LegCreateService extends AbstractGuiService<Manager, Leg> {

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
		flight = (Flight) this.flightRepository.findById(flightId).get();
		manager = (Manager) super.getRequest().getPrincipal().getActiveRealm();

		if (manager.getAirline().getId() == flight.getAirline().getId())
			authorise = true;
		super.getResponse().setAuthorised(authorise);
	}

	@Override
	public void load() {
		Leg leg = new Leg();
		int flightId = super.getRequest().getData("flightId", int.class);
		Flight flight = (Flight) this.flightRepository.findById(flightId).get();

		leg.setFlight(flight);
		leg.setDraftMode(true);
		super.getBuffer().addData(leg);
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

		// R1: el momento de llegada sea posterior al momento de salida sin usar el reloj real
		if (leg.getScheduledArrival() != null && leg.getScheduledDeparture() != null) {
			boolean correctDepatureArrivalDate = MomentHelper.isAfter(leg.getScheduledArrival(), leg.getScheduledDeparture());
			super.state(correctDepatureArrivalDate, "scheduledArrival", "acme.validation.leg.wrong-scheduled-arrival.message");
		}

		// R2: a unique flight number composed of the airline's IATA code followed by four digits, unique
		Manager manager = (Manager) super.getRequest().getPrincipal().getActiveRealm();
		String airlineCode = manager.getAirline().getIataCode();
		Boolean correctFlightNumber = StringHelper.startsWith(leg.getFlightNumber(), airlineCode, false);
		super.state(correctFlightNumber, "flightNumber", "acme.validation.leg.wrong-flight-number.message");

		//R3: no puede existir otro leg con el mismo flight number
		String flightNumber = leg.getFlightNumber();
		boolean isUnique = !this.repository.existsByFlightNumber(flightNumber);
		super.state(isUnique, "flightNumber", "acme.validation.leg.duplicated-code.message");

		//R4: no puede ser el mismo aeropuerto de llegada que el de salida
		if (leg.getArrivalAirport() != null && leg.getDepartureAirport() != null) {
			boolean isDifferent = !leg.getArrivalAirport().equals(leg.getDepartureAirport());
			super.state(isDifferent, "*", "acme.validation.leg.same-airports.message");
		}

		//R5: requisito de confirmacion
		boolean confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");
	}

	@Override
	public void perform(final Leg leg) {
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
		SelectChoices departureChoices = SelectChoices.from(airports, "name", leg.getDepartureAirport());
		SelectChoices arrivalChoices = SelectChoices.from(airports, "name", leg.getArrivalAirport());

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
