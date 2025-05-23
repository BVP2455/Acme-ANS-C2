
package acme.features.manager.leg;

import java.util.Collection;

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

		if (authorise) {
			String method;
			int arrivalAirportId, departureAirportId, aircraftId, airlineId;
			Aircraft aircraft;
			Airport arrivalAirport;
			Airport departureAirport;

			method = super.getRequest().getMethod();

			if (method.equals("GET"))
				authorise = true;
			else {
				airlineId = manager.getAirline().getId();
				aircraftId = super.getRequest().getData("aircraft", int.class);
				arrivalAirportId = super.getRequest().getData("airportArrival", int.class);
				departureAirportId = super.getRequest().getData("airportDeparture", int.class);
				aircraft = this.repository.findAircraftByAirlineId(airlineId, aircraftId);
				arrivalAirport = this.repository.findAirportByAirportId(arrivalAirportId);
				departureAirport = this.repository.findAirportByAirportId(departureAirportId);
				authorise = (aircraftId == 0 || aircraft != null) && (arrivalAirportId == 0 || arrivalAirport != null) && (departureAirportId == 0 || departureAirport != null);

			}
		}

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

		// R1: momento de salida deben se posterior a la fecha actual
		if (leg.getScheduledDeparture() != null) {
			boolean futureDepartureDate = MomentHelper.isFuture(leg.getScheduledDeparture());
			super.state(futureDepartureDate, "scheduledDeparture", "acme.validation.leg.scheduled-departure-not-future.message");
		}
		//R3: no puede existir otro leg con el mismo flight number
		String flightNumber = leg.getFlightNumber();
		boolean isUnique = !this.repository.existsByFlightNumber(flightNumber);
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
