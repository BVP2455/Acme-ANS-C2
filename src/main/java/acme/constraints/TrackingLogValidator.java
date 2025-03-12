
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.entities.trackingLog.TrackingLog;
import acme.entities.trackingLog.TrackingLogRepository;
import acme.entities.trackingLog.TrackingLogStatus;

@Validator
public class TrackingLogValidator extends AbstractValidator<ValidTrackingLog, TrackingLog> {

	@Autowired
	private TrackingLogRepository repository;


	@Override
	protected void initialise(final ValidTrackingLog annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final TrackingLog trackingLog, final ConstraintValidatorContext context) {
		assert context != null;

		boolean result;
		if (trackingLog == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
		if (trackingLog.getClaim() == null)
			super.state(context, false, "*", "javax.validation.trackinglog.null-claim.message");
		else {
			Double lastResolutionPercentage = this.repository.getLatestTrackingLog(trackingLog.getClaim().getId()).getResolutionPercentage();
			if ((trackingLog.getStatus() == TrackingLogStatus.ACCEPTED || trackingLog.getStatus() == TrackingLogStatus.REJECTED) && trackingLog.getResolutionPercentage() < 100)
				super.state(context, false, "*", "javax.validation.trackinglog.incorrect-status.message");
			else if (!(trackingLog.getStatus() == TrackingLogStatus.ACCEPTED || trackingLog.getStatus() == TrackingLogStatus.REJECTED) && trackingLog.getResolutionPercentage() == 100)
				super.state(context, false, "*", "javax.validation.trackinglog.incorrect-status-pending.message");
			else if (lastResolutionPercentage > trackingLog.getResolutionPercentage())
				super.state(context, false, "*", "javax.validation.trackinglog.incorrect-resolutionpercentage.message");
		}
		result = !super.hasErrors(context);
		return result;
	}

}
