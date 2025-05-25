
package acme.features.assistanceAgent.trackingLog;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claim.Claim;
import acme.entities.trackingLog.TrackingLog;
import acme.entities.trackingLog.TrackingLogStatus;
import acme.realms.assistanceAgents.AssistanceAgent;

@GuiService
public class AssistanceAgentTrackingLogCreateService extends AbstractGuiService<AssistanceAgent, TrackingLog> {

	@Autowired
	private AssistanceAgentTrackingLogRepository repository;


	@Override
	public void authorise() {
		boolean status;
		Claim claim = null;
		Integer claimId = super.getRequest().getData("claimId", Integer.class);
		if (claimId != null)
			claim = this.repository.findClaimById(claimId);
		status = claim != null && super.getRequest().getPrincipal().hasRealm(claim.getAssistanceAgent());
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		TrackingLog trackingLog;
		Integer claimId = super.getRequest().getData("claimId", Integer.class);
		Claim claim = this.repository.findClaimById(claimId);

		trackingLog = new TrackingLog();
		trackingLog.setDraftMode(true);
		trackingLog.setLastUpdateMoment(MomentHelper.getCurrentMoment());
		trackingLog.setRegistrationMoment(MomentHelper.getCurrentMoment());
		trackingLog.setClaim(claim);
		trackingLog.setStatus(TrackingLogStatus.PENDING);

		super.getBuffer().addData(trackingLog);
	}

	@Override
	public void bind(final TrackingLog trackingLog) {
		super.bindObject(trackingLog, "step", "resolutionPercentage", "status", "resolution");

	}

	@Override
	public void validate(final TrackingLog trackingLog) {
		boolean valid;
		TrackingLogStatus status = trackingLog.getStatus();
		String resolution = trackingLog.getResolution();

		if (trackingLog.getResolutionPercentage() != null && status != null)
			if (trackingLog.getResolutionPercentage() < 100.0) {
				valid = status.equals(TrackingLogStatus.PENDING);
				super.state(valid, "status", "assistanceAgent.trackingLog.form.error.incorrect-status-pending");
			} else {
				valid = !status.equals(TrackingLogStatus.PENDING);
				super.state(valid, "status", "assistanceAgent.trackingLog.form.error.incorrect-status");
			}

		if (status != null)
			if (status.equals(TrackingLogStatus.PENDING)) {
				valid = resolution == null || resolution.isBlank();
				super.state(valid, "resolution", "assistanceAgent.trackingLog.form.error.incorrect-resolution-pending");
			} else {
				valid = resolution != null && !resolution.isBlank();
				super.state(valid, "resolution", "assistanceAgent.trackingLog.form.error.incorrect-resolution");
			}

		if (trackingLog.getClaim() != null && trackingLog.getResolutionPercentage() != null) {
			List<TrackingLog> trackingLogs = this.repository.findLastTrackingLogByClaimId(trackingLog.getClaim().getId());
			if (!trackingLogs.isEmpty()) {
				TrackingLog latestLog = trackingLogs.get(0);
				boolean isSameTrackingLog = latestLog.getId() == trackingLog.getId();
				if (!isSameTrackingLog) {
					boolean bothComplete = latestLog.getResolutionPercentage() == 100 && trackingLog.getResolutionPercentage() == 100;
					if (bothComplete) {
						long logs = trackingLogs.stream().filter(log -> log.getResolutionPercentage() == 100).count();
						valid = !latestLog.getDraftMode() && logs < 2;
						super.state(valid, "resolutionPercentage", "assistanceAgent.trackingLog.form.error.incorrect-number-logs");
					} else {
						valid = latestLog.getResolutionPercentage() < trackingLog.getResolutionPercentage();
						super.state(valid, "resolutionPercentage", "assistanceAgent.trackingLog.form.error.incorrect-percentage");
					}
				}
			}
		}
	}

	@Override
	public void perform(final TrackingLog trackingLog) {
		this.repository.save(trackingLog);
	}

	@Override
	public void unbind(final TrackingLog trackingLog) {
		SelectChoices statusChoices;
		Dataset dataset;
		statusChoices = SelectChoices.from(TrackingLogStatus.class, trackingLog.getStatus());
		dataset = super.unbindObject(trackingLog, "lastUpdateMoment", "step", "resolutionPercentage", "status", "resolution", "draftMode", "id");
		dataset.put("statusChoices", statusChoices);
		int claimId = super.getRequest().getData("claimId", int.class);
		dataset.put("claimId", claimId);
		super.getResponse().addData(dataset);
	}

}
