
package acme.features.assistanceAgent.trackingLog;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claim.Claim;
import acme.entities.trackingLog.TrackingLog;
import acme.realms.assistanceAgents.AssistanceAgent;

@GuiService
public class AssistanceAgentTrackingLogListService extends AbstractGuiService<AssistanceAgent, TrackingLog> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AssistanceAgentTrackingLogRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		if (super.getRequest().getMethod().equals("GET")) {
			Integer claimId = super.getRequest().getData("claimId", Integer.class);
			Claim claim = this.repository.findClaimById(claimId);
			status = claim != null && super.getRequest().getPrincipal().hasRealm(claim.getAssistanceAgent());
			super.getResponse().setAuthorised(status);
		} else
			super.getResponse().setAuthorised(false);
	}

	@Override
	public void load() {
		Collection<TrackingLog> trackingLogs;
		int assistanceAgentId;
		assistanceAgentId = super.getRequest().getPrincipal().getActiveRealm().getId();
		if (super.getRequest().getData().isEmpty())
			trackingLogs = this.repository.findAllTrackingLogs(assistanceAgentId);
		else {
			int claimId = super.getRequest().getData("claimId", int.class);
			trackingLogs = this.repository.findTrackingLogsByClaimId(claimId);
		}

		super.getBuffer().addData(trackingLogs);
	}

	@Override
	public void unbind(final TrackingLog trackingLog) {
		Dataset dataset;
		dataset = super.unbindObject(trackingLog, "registrationMoment", "lastUpdateMoment", "step", "resolutionPercentage", "status", "resolution");
		int claimId = super.getRequest().getData("claimId", int.class);
		super.getResponse().addGlobal("claimId", claimId);
		super.getResponse().addData(dataset);
	}

}
