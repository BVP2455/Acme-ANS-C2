
package acme.constraints;

import java.util.List;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.entities.claim.Claim;
import acme.entities.trackingLog.TrackingLog;
import acme.entities.trackingLog.TrackingLogRepository;

@Validator
public class ClaimValidator extends AbstractValidator<ValidClaim, Claim> {

	@Autowired
	private TrackingLogRepository repository;


	@Override
	protected void initialise(final ValidClaim annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Claim claim, final ConstraintValidatorContext context) {
		assert context != null;

		boolean result;
		if (claim == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");

		List<TrackingLog> trackingLogs = this.repository.findTrackingLogsByClaimId(claim.getId());
		for (int i = 0; i < trackingLogs.size() - 1; i++)
			if (trackingLogs.get(i).getResolutionPercentage() > trackingLogs.get(i + 1).getResolutionPercentage())
				super.state(context, false, "*", "acme.validation.claim.resolutionPercentage.message");

		result = !super.hasErrors(context);
		return result;
	}
}
