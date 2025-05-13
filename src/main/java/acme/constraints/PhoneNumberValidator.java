
package acme.constraints;

import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PhoneNumberValidator implements ConstraintValidator<ValidPhoneNumber, String> {

	private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?\\d{6,15}$");


	@Override
	public boolean isValid(final String value, final ConstraintValidatorContext context) {
		if (value == null || value.trim().isEmpty())
			return true;

		return PhoneNumberValidator.PHONE_PATTERN.matcher(value).matches();
	}
}
