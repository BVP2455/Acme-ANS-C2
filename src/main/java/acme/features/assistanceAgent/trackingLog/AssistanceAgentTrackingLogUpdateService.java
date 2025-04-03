
package acme.features.assistanceAgent.trackingLog;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claim.Claim;
import acme.entities.trackingLog.TrackingLog;
import acme.entities.trackingLog.TrackingLogStatus;
import acme.realms.assistanceAgents.AssistanceAgent;

@GuiService
public class AssistanceAgentTrackingLogUpdateService extends AbstractGuiService<AssistanceAgent, TrackingLog> {

	@Autowired
	private AssistanceAgentTrackingLogRepository repository;


	@Override
	public void authorise() {
		boolean status;
		TrackingLog trackingLog;
		int id;
		AssistanceAgent assistanceAgent;

		id = super.getRequest().getData("id", int.class);
		trackingLog = this.repository.findTrackingLogById(id);
		assistanceAgent = trackingLog == null ? null : trackingLog.getClaim().getAssistanceAgent();
		status = super.getRequest().getPrincipal().hasRealm(assistanceAgent);

		super.getResponse().setAuthorised(status);

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
		super.bindObject(trackingLog, "lastUpdateMoment", "step", "resolutionPercentage", "status", "resolution", "claim");

	}

	@Override
	public void validate(final TrackingLog trackingLog) {
		boolean valid;

		if (trackingLog.getResolutionPercentage() < 100.0) {
			valid = trackingLog.getStatus().equals(TrackingLogStatus.PENDING);
			super.state(valid, "status", "assistanceAgent.trackingLog.form.error.incorrect-status-pending");
		} else {
			valid = !trackingLog.getStatus().equals(TrackingLogStatus.PENDING);
			super.state(valid, "status", "assistanceAgent.trackingLog.form.error.incorrect-status");
		}

		List<TrackingLog> trackingLogs = this.repository.findLastPercentageTrackingLogPublished(trackingLog.getClaim().getId());

		if (!trackingLogs.isEmpty()) {
			long resolvedTrackingLogs = trackingLogs.stream().filter(t -> t.getResolutionPercentage() == 100).count();
			if (trackingLogs.get(0).getId() != trackingLog.getId())
				if (trackingLogs.get(0).getResolutionPercentage() == 100 && trackingLog.getResolutionPercentage() == 100) {
					valid = !trackingLogs.get(0).getDraftMode() && resolvedTrackingLogs < 2;
					super.state(valid, "resolutionPercentage", "assistanceAgent.trackingLog.form.error.incorrect-number-logs");
				} else {
					valid = trackingLogs.get(0).getResolutionPercentage() < trackingLog.getResolutionPercentage();
					super.state(valid, "resolutionPercentage", "assistanceAgent.trackingLog.form.error.incorrect-percentage");
				}
		}
	}

	@Override
	public void perform(final TrackingLog trackingLog) {
		System.out.println("Antes de guardar: " + trackingLog);
		this.repository.save(trackingLog);
		System.out.println("Después de guardar: " + this.repository.findTrackingLogById(trackingLog.getId()));
	}

	@Override
	public void unbind(final TrackingLog trackingLog) {

		Collection<Claim> claims;
		SelectChoices statusChoices;
		SelectChoices claimChoices;
		Dataset dataset;
		int assistanceAgentId;
		assistanceAgentId = super.getRequest().getPrincipal().getActiveRealm().getId();
		statusChoices = SelectChoices.from(TrackingLogStatus.class, trackingLog.getStatus());

		claims = this.repository.findClaimsByAssistanceAgent(assistanceAgentId);
		claimChoices = SelectChoices.from(claims, "id", trackingLog.getClaim());

		dataset = super.unbindObject(trackingLog, "claim", "lastUpdateMoment", "step", "resolutionPercentage", "status", "resolution", "draftMode", "id");
		dataset.put("statusChoices", statusChoices);
		dataset.put("claimChoices", claimChoices);

		super.getResponse().addData(dataset);

	}

}
