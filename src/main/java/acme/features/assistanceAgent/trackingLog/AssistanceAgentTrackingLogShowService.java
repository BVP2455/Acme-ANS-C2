
package acme.features.assistanceAgent.trackingLog;

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
public class AssistanceAgentTrackingLogShowService extends AbstractGuiService<AssistanceAgent, TrackingLog> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AssistanceAgentTrackingLogRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		if (super.getRequest().getMethod().equals("GET")) {
			Integer trackingLogId = super.getRequest().getData("id", Integer.class);
			TrackingLog trackingLog = null;
			if (trackingLogId != null)
				trackingLog = this.repository.findTrackingLogById(trackingLogId);
			AssistanceAgent assistanceAgent = trackingLog == null ? null : trackingLog.getClaim().getAssistanceAgent();
			status = super.getRequest().getPrincipal().hasRealm(assistanceAgent);
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
