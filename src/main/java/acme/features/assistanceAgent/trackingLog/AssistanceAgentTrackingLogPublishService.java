
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
public class AssistanceAgentTrackingLogPublishService extends AbstractGuiService<AssistanceAgent, TrackingLog> {

	@Autowired
	private AssistanceAgentTrackingLogRepository repository;


	@Override
	public void authorise() {
		boolean status;
		if (super.getRequest().getMethod().equals("POST")) {
			Integer trackingLogId = super.getRequest().getData("id", Integer.class);
			TrackingLog trackingLog = null;
			if (trackingLogId != null)
				trackingLog = this.repository.findTrackingLogById(trackingLogId);
			AssistanceAgent assistanceAgent = trackingLog == null ? null : trackingLog.getClaim().getAssistanceAgent();
			status = super.getRequest().getPrincipal().hasRealm(assistanceAgent) && (trackingLog == null || trackingLog.getDraftMode());
			super.getResponse().setAuthorised(status);
		} else
			super.getResponse().setAuthorised(false);
	}

	@Override
	public void load() {
		TrackingLog trackingLog;
		int id;

		id = super.getRequest().getData("id", int.class);
		trackingLog = this.repository.findTrackingLogById(id);

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

		List<TrackingLog> trackingLogs = this.repository.findLastPercentageTrackingLogPublished(trackingLog.getClaim().getId());
		if (!trackingLogs.isEmpty()) {
			TrackingLog highest = trackingLogs.get(0);
			boolean isSameTrackingLog = highest.getId() == trackingLog.getId();
			if (!isSameTrackingLog && trackingLog.getResolutionPercentage() != null)
				if (highest.getResolutionPercentage() == 100.0 && trackingLog.getResolutionPercentage() == 100.0) {
					long logs = trackingLogs.stream().filter(t -> t.getResolutionPercentage() == 100).count();
					valid = !highest.getDraftMode() && logs < 2;
					super.state(valid, "resolutionPercentage", "assistanceAgent.trackingLog.form.error.incorrect-number-logs");
				} else {
					valid = highest.getResolutionPercentage() < trackingLog.getResolutionPercentage();
					super.state(valid, "resolutionPercentage", "assistanceAgent.trackingLog.form.error.incorrect-percentage");
				}
		}
	}

	@Override
	public void perform(final TrackingLog trackingLog) {
		trackingLog.setDraftMode(false);
		trackingLog.setLastUpdateMoment(MomentHelper.getCurrentMoment());
		this.repository.save(trackingLog);
	}

	@Override
	public void unbind(final TrackingLog trackingLog) {
		SelectChoices statusChoices;
		Dataset dataset;
		statusChoices = SelectChoices.from(TrackingLogStatus.class, trackingLog.getStatus());
		dataset = super.unbindObject(trackingLog, "lastUpdateMoment", "step", "resolutionPercentage", "status", "resolution", "draftMode", "id");
		dataset.put("statusChoices", statusChoices);
		Claim claim = this.repository.findClaimByTrackingLogId(trackingLog.getId());
		dataset.put("claimId", claim.getId());
		super.getResponse().addData(dataset);
	}

}
