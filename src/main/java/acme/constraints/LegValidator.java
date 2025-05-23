
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.client.helpers.MomentHelper;
import acme.client.helpers.StringHelper;
import acme.entities.aircraft.Status;
import acme.entities.airline.Airline;
import acme.entities.flight.Flight;
import acme.entities.leg.Leg;
import acme.features.manager.leg.LegRepository;

@Validator
public class LegValidator extends AbstractValidator<ValidLeg, Leg> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private LegRepository repository;

	// ConstraintValidator interface ------------------------------------------


	@Override
	protected void initialise(final ValidLeg annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Leg leg, final ConstraintValidatorContext context) {
		assert context != null;

		Flight flight = leg.getFlight();
		Airline airline = leg.getFlight().getAirline();

		boolean result;

		if (leg == null || flight == null)
			super.state(context, false, "*", "acme.validation.leg.NotNull.message");
		else {

			// R1: el momento de llegada sea posterior al momento de salida
			if (leg.getScheduledArrival() != null && leg.getScheduledDeparture() != null) {
				boolean correctDepatureArrivalDate = MomentHelper.isAfterOrEqual(leg.getScheduledArrival(), leg.getScheduledDeparture());
				super.state(context, correctDepatureArrivalDate, "scheduledArrival", "acme.validation.leg.wrong-scheduled-arrival.message");
			}

			// R2: a unique flight number composed of the airline's IATA code followed by four digits, unique
			String airlineCode = airline.getIataCode();
			boolean correctFlightNumber = StringHelper.startsWith(leg.getFlightNumber(), airlineCode, false);
			super.state(context, correctFlightNumber, "flightNumber", "acme.validation.leg.wrong-flight-number.message");

			//R4: la aeronave debe estar en estado ACTIVE
			if (leg.getAircraft() != null && leg.getAircraft().getStatus() != null && leg.getAircraft().getStatus() != Status.ACTIVE)
				super.state(context, false, "aircraft", "acme.validation.leg.aircraft-not-active.message");

		}

		result = !super.hasErrors(context);

		return result;
	}
}
