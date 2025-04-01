
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.principals.DefaultUserIdentity;
import acme.client.components.validation.AbstractValidator;
import acme.client.helpers.StringHelper;
import acme.realms.manager.Manager;
import acme.realms.manager.ManagerRepository;

public class ManagerValidator extends AbstractValidator<ValidManager, Manager> {

	// Internal state ---------------------------------------------

	@Autowired
	private ManagerRepository repository;

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

		if (manager == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
		else if (StringHelper.isBlank(manager.getIdentifier()))
			super.state(context, false, "identifier", "javax.validation.constraints.NotBlank.message");
		else {

			//R1: el identificador no puede ser repetido
			boolean uniqueManager;
			Manager existingManager;

			existingManager = this.repository.findManagerByIdentifier(manager.getIdentifier());
			uniqueManager = existingManager == null || existingManager.equals(manager);
			super.state(context, uniqueManager, "ticker", "acme.validation.manager.duplicated-identifier.message");

			//R2: el comienzo del identificador debe contener la primera inicial de su nombre seguida de la de su apellido
			boolean containsInitials = false;
			DefaultUserIdentity identity = manager.getIdentity();
			if (identity != null) {
				char nameFirstLetter = identity.getName().charAt(0);
				char surnameFirstLetter = identity.getSurname().charAt(0);
				String initials = "" + nameFirstLetter + surnameFirstLetter;
				containsInitials = StringHelper.startsWith(manager.getIdentifier(), initials, false);
			}
			super.state(context, containsInitials, "identifier", "acme.validation.manager.identifier.message");
		}

		result = !super.hasErrors(context);
		return result;
	}
}
