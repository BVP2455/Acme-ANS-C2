
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.client.helpers.MomentHelper;
import acme.client.helpers.StringHelper;
import acme.entities.legs.Leg;
import acme.entities.legs.LegRepository;

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

		boolean result;

		if (leg == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
		else {
			{
				// R1: hay que hacer una validacion custom para que el momento de llegada sea posterior al momento de salida sin usar el reloj real
				Boolean correctDepatureArrivalDate = MomentHelper.isAfter(leg.getScheduledArrival(), leg.getScheduledDeparture());

				super.state(context, correctDepatureArrivalDate, "scheduledArrival", "acme.validation.leg.wrong-scheduled-arrival.message");
			}
			{
				// R2: a unique flight number composed of the airline's IATA code followed by four digits, unique
				String airlineCode = leg.getAirline().getCode();
				Boolean correctFlightNumber = StringHelper.startsWith(leg.getFlightNumber(), airlineCode, false);

				super.state(context, correctFlightNumber, "flightNumber", "acme.validation.leg.wrong-flight-number.message");
			}

		}

		result = !super.hasErrors(context);

		return result;
	}
}
