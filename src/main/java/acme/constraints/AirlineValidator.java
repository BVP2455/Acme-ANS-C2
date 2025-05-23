
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.helpers.StringHelper;
import acme.entities.airline.Airline;
import acme.features.administrator.airline.AirlineRepository;

public class AirlineValidator extends AbstractValidator<ValidAirline, Airline> {

	// Internal state ---------------------------------------------

	@Autowired
	private AirlineRepository repository;

	// ConstraintValidator interface ------------------------------


	@Override
	protected void initialise(final ValidAirline annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Airline airline, final ConstraintValidatorContext context) {
		// HINT: value can be null
		assert context != null;

		boolean result;

		if (airline == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
		else if (StringHelper.isBlank(airline.getIataCode()))
			super.state(context, false, "iataCode", "javax.validation.constraints.NotBlank.message");

		result = !super.hasErrors(context);
		return result;
	}
}
