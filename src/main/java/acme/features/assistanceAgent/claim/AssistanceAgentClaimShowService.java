
package acme.features.assistanceAgent.claim;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claim.Claim;
import acme.entities.claim.ClaimType;
import acme.entities.leg.Leg;
import acme.entities.trackingLog.TrackingLogStatus;
import acme.realms.assistanceAgents.AssistanceAgent;

@GuiService
public class AssistanceAgentClaimShowService extends AbstractGuiService<AssistanceAgent, Claim> {

	@Autowired
	private ClaimRepository repository;


	@Override
	public void authorise() {
		boolean status;
		if (super.getRequest().getMethod().equals("GET")) {
			status = super.getRequest().getPrincipal().hasRealmOfType(AssistanceAgent.class);
			super.getResponse().setAuthorised(status);
			Integer agentId = super.getRequest().getPrincipal().getActiveRealm().getId();
			Integer claimId = super.getRequest().getData("id", Integer.class);
			if (claimId == null)
				super.getResponse().setAuthorised(false);
			else {
				Claim claim = this.repository.findClaimById(claimId);
				super.getResponse().setAuthorised(agentId == claim.getAssistanceAgent().getId());
			}
		} else
			super.getResponse().setAuthorised(false);
	}

	@Override
	public void load() {
		Claim claim;
		int id;

		id = super.getRequest().getData("id", int.class);
		claim = this.repository.findClaimById(id);

		super.getBuffer().addData(claim);
	}

	@Override
	public void unbind(final Claim claim) {
		Collection<Leg> legs;
		SelectChoices choices;
		SelectChoices choices2;
		Dataset dataset;
		TrackingLogStatus status;

		status = claim.getStatus();
		choices = SelectChoices.from(ClaimType.class, claim.getType());
		legs = this.repository.findAllLegPublish();
		choices2 = SelectChoices.from(legs, "flightNumber", claim.getLeg());

		dataset = super.unbindObject(claim, "registrationMoment", "passengerEmail", "description", "type", "draftMode", "id");
		dataset.put("types", choices);
		dataset.put("leg", choices2.getSelected().getKey());
		dataset.put("legs", choices2);
		dataset.put("status", status);

		super.getResponse().addData(dataset);
	}

}
