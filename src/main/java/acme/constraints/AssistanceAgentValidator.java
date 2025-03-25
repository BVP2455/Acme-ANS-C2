
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.principals.DefaultUserIdentity;
import acme.client.components.validation.AbstractValidator;
import acme.client.helpers.StringHelper;
import acme.realms.assistanceAgents.AssistanceAgent;
import acme.realms.assistanceAgents.AssistanceAgentRepository;

public class AssistanceAgentValidator extends AbstractValidator<ValidAssistanceAgent, AssistanceAgent> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private AssistanceAgentRepository repository;

	// Initialiser ------------------------------------------------------------


	@Override
	public void initialise(final ValidAssistanceAgent annotation) {
		assert annotation != null;
	}

	// AbstractValidator interface --------------------------------------------

	@Override
	public boolean isValid(final AssistanceAgent assistanceAgent, final ConstraintValidatorContext context) {
		// HINT: value can be null
		assert context != null;

		boolean result;

		if (assistanceAgent == null || assistanceAgent.getEmployeeCode() == null || assistanceAgent.getIdentity() == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
		else if (StringHelper.isBlank(assistanceAgent.getEmployeeCode()))
			super.state(context, false, "identifier", "javax.validation.constraints.NotBlank.message");
		else {
			boolean containsInitials;
			DefaultUserIdentity identity = assistanceAgent.getIdentity();
			char nameFirstLetter = identity.getName().charAt(0);
			char surnameFirstLetter = identity.getSurname().charAt(0);
			String initials = "" + nameFirstLetter + surnameFirstLetter;
			containsInitials = StringHelper.startsWith(assistanceAgent.getEmployeeCode(), initials, false); //Comprueba que empiece por las 2 iniciales
			super.state(context, containsInitials, "identifier", "acme.validation.assistanceAgent.identifier.message");
		}

		if (this.repository != null) {
			String employeeCode = assistanceAgent.getEmployeeCode();
			AssistanceAgent existingAgent = this.repository.findByEmployeeCode(employeeCode);
			boolean uniqueEmployeeCode = existingAgent == null || existingAgent.equals(assistanceAgent);

			super.state(context, uniqueEmployeeCode, "employeeCode", "acme.validation.assistanceAgent.duplicated-employeeCode.message");
		}

		result = !super.hasErrors(context);
		return result;
	}
}
