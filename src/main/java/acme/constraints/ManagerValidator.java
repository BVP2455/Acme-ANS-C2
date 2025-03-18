
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.principals.DefaultUserIdentity;
import acme.client.components.validation.AbstractValidator;
import acme.client.helpers.StringHelper;
import acme.realms.Manager;

public class ManagerValidator extends AbstractValidator<ValidManager, Manager> {

	// Internal state ---------------------------------------------

	// ConstraintValidator interface ------------------------------

	@Override
	protected void initialise(final ValidManager annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Manager manager, final ConstraintValidatorContext context) {
		// HINT: value can be null
		assert context != null;

		boolean result;

		if (manager == null || manager.getIdentifier() == null || manager.getIdentity() == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
		else if (StringHelper.isBlank(manager.getIdentifier()))
			super.state(context, false, "identifier", "javax.validation.constraints.NotBlank.message");
		else {
			boolean containsInitials;
			DefaultUserIdentity identity = manager.getIdentity();
			String[] nombreCompleto = (identity.getName() + " " + identity.getSurname()).trim().split("\\s+");
			StringBuilder initials = new StringBuilder();

			for (String palabra : nombreCompleto)
				if (!palabra.isEmpty())
					initials.append(palabra.charAt(0));

			containsInitials = StringHelper.startsWith(manager.getIdentifier(), initials.toString(), false);
			super.state(context, containsInitials, "identifier", "acme.validation.customer.identifier.message");
		}

		result = !super.hasErrors(context);
		return result;
	}
}
