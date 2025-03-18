
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.principals.DefaultUserIdentity;
import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.client.helpers.StringHelper;
import acme.realms.Customer;

@Validator
public class CustomerValidator extends AbstractValidator<ValidCustomer, Customer> {
	// Internal state ---------------------------------------------------------

	// Initialiser ------------------------------------------------------------

	@Override
	public void initialise(final ValidCustomer annotation) {
		assert annotation != null;
	}

	// AbstractValidator interface --------------------------------------------

	@Override
	public boolean isValid(final Customer customer, final ConstraintValidatorContext context) {
		// HINT: value can be null
		assert context != null;

		boolean result;

		if (customer == null || customer.getIdentifier() == null || customer.getIdentity() == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
		else if (StringHelper.isBlank(customer.getIdentifier()))
			super.state(context, false, "identifier", "javax.validation.constraints.NotBlank.message");
		else {
			boolean containsInitials;
			DefaultUserIdentity identity = customer.getIdentity();

			// Obtener las iniciales de todas las palabras del nombre completo
			String[] nombreCompleto = (identity.getName() + " " + identity.getSurname()).trim().split("\\s+");
			StringBuilder initials = new StringBuilder();

			for (String palabra : nombreCompleto)
				if (!palabra.isEmpty())
					initials.append(palabra.charAt(0));

			containsInitials = StringHelper.startsWith(customer.getIdentifier(), initials.toString(), false);
			super.state(context, containsInitials, "identifier", "acme.validation.customer.identifier.message");
		}

		result = !super.hasErrors(context);
		return result;
	}
}
