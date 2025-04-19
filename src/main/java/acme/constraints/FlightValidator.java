
package acme.constraints;

import java.util.List;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.client.helpers.MomentHelper;
import acme.client.helpers.SpringHelper;
import acme.entities.flight.Flight;
import acme.entities.leg.Leg;
import acme.features.manager.leg.LegRepository;

@Validator
public class FlightValidator extends AbstractValidator<ValidFlight, Flight> {

	@Override
	protected void initialise(final ValidFlight annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Flight flight, final ConstraintValidatorContext context) {

		assert context != null;

		boolean result;

		Boolean draftMode = flight.getDraftMode();
		LegRepository repository;
		repository = SpringHelper.getBean(LegRepository.class);
		List<Leg> legs = repository.findLegsByFlightOrderedByDeparture(flight.getId());

		if (flight == null || legs == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
		else if (draftMode == false)
			for (int i = 0; i < legs.size() - 1; i++) {
				Leg currentLeg = legs.get(i);
				Leg nextLeg = legs.get(i + 1);

				if (MomentHelper.isAfter(currentLeg.getScheduledArrival(), nextLeg.getScheduledDeparture()))
					super.state(context, false, "*", "acme.validation.flight.legs.sequential-order");
				if (flight.getNumberLayovers() < 0)
					super.state(context, false, "*", "acme.validation.flight.legs.negative-layovers");
			}
		result = !super.hasErrors(context);

		return result;
	}
}
