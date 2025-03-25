
package acme.constraints;

import java.util.Calendar;
import java.util.Date;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.client.helpers.MomentHelper;
import acme.client.helpers.StringHelper;
import acme.entities.Service;

@Validator
public class ServiceValidator extends AbstractValidator<ValidService, Service> {

	@Override
	protected void initialise(final ValidService annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Service service, final ConstraintValidatorContext context) {
		assert context != null;

		if (service == null) {
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
			return false;
		}

		String promotionCode = service.getPromotionCode();
		if (promotionCode == null || promotionCode.isBlank())
			return true;

		boolean containsYear;
		Date currentMoment = MomentHelper.getCurrentMoment();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(currentMoment);
		Integer currentYear = calendar.get(Calendar.YEAR);
		String year = String.valueOf(currentYear).substring(2);
		containsYear = StringHelper.endsWith(promotionCode, year, false); // Comprueba que termine con los 2 dígitos del año

		super.state(context, containsYear, "identifier", "acme.validation.service.promotioncode.year.message");

		return !super.hasErrors(context);
	}
}
