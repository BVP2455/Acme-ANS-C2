
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
		assert context != null;

		boolean result;

		if (manager == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
		else {

			//R1: the first two or three letters of the identifier number must correspond to their initials
			StringBuilder iniciales = new StringBuilder();
			DefaultUserIdentity identity = manager.getIdentity();
			String[] nombreCompleto = identity.getFullName().trim().split(" ");
			for (int i = 0; i < nombreCompleto.length; i++)
				iniciales.append(nombreCompleto[i].charAt(0));
			Boolean identificadorCorrecto = StringHelper.startsWith(manager.getIdentifier(), iniciales.toString().substring(0, 2), true) || StringHelper.startsWith(manager.getIdentifier(), iniciales.toString().substring(0, 3), true);

			super.state(context, identificadorCorrecto, "*", "javax.validation.manager.wrong-identifier-number.message");

		}

		result = !super.hasErrors(context);

		return result;
	}

}
