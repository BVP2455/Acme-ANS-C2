
package acme.constraints;

import java.util.List;

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

		if (trackingLog == null) {
			super.state(context, false, "", "javax.validation.constraints.NotNull.message");
			return false;
		}

		if (trackingLog.getClaim() == null) {
			super.state(context, false, "", "javax.validation.constraints.NotNull.message");
			return false;
		}

		List<TrackingLog> trackingLogs = this.repository.findTrackingLogsByClaimId(trackingLog.getClaim().getId());
		Double percentage = trackingLog.getResolutionPercentage();
		TrackingLogStatus status = trackingLog.getStatus();
		boolean hasTrackingLogs = !trackingLogs.isEmpty();
		if (hasTrackingLogs && percentage != null && status != null) {
			if ((status == TrackingLogStatus.ACCEPTED || status == TrackingLogStatus.REJECTED) && percentage < 100)
				super.state(context, false, "status", "acme.validation.trackinglog.incorrect-status.message");
			if (!(status == TrackingLogStatus.ACCEPTED || status == TrackingLogStatus.REJECTED) && percentage == 100)
				super.state(context, false, "status", "acme.validation.trackinglog.incorrect-status-pending.message");
		}
		if (trackingLog.getRegistrationMoment() != null && trackingLog.getLastUpdateMoment() != null)
			if (trackingLog.getRegistrationMoment().after(trackingLog.getLastUpdateMoment()))
				super.state(context, false, "lastUpdateMoment", "acme.validation.trackinglog.incorrect-lastUpdateMoment.message");
		return !super.hasErrors(context);
	}

}
