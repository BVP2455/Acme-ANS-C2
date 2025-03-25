
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.client.helpers.MomentHelper;
import acme.entities.activitylog.ActivityLog;

public class ActivityLogValidator extends AbstractValidator<ValidActivityLog, ActivityLog> {

	@Override
	public void initialise(final ValidActivityLog annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final ActivityLog activityLog, final ConstraintValidatorContext context) {

		assert context != null;

		boolean result;

		if (activityLog == null || activityLog.getActivityLogAssignment() == null || activityLog.getActivityLogAssignment().getLeg() == null || activityLog.getActivityLogAssignment().getLeg().getScheduledArrival() == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
		else {
			boolean registrationMomentIsAfterArrivalLeg;
			registrationMomentIsAfterArrivalLeg = MomentHelper.isAfterOrEqual(activityLog.getRegistrationMoment(), activityLog.getActivityLogAssignment().getLeg().getScheduledArrival());
			super.state(context, registrationMomentIsAfterArrivalLeg, "registrationMoment", "acme.validation.activitylog.registrationmoment.message");
		}

		result = !super.hasErrors(context);
		return result;
	}
}
