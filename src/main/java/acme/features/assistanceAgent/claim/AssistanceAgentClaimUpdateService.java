
package acme.features.assistanceAgent.claim;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claim.Claim;
import acme.entities.claim.ClaimType;
import acme.entities.leg.Leg;
import acme.realms.assistanceAgents.AssistanceAgent;

@GuiService
public class AssistanceAgentClaimUpdateService extends AbstractGuiService<AssistanceAgent, Claim> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ClaimRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		if (super.getRequest().getMethod().equals("POST")) {
			Integer claimId = super.getRequest().getData("id", Integer.class);
			Claim claim = this.repository.findClaimById(claimId);
			AssistanceAgent assistanceAgent = claim == null ? null : claim.getAssistanceAgent();
			status = super.getRequest().getPrincipal().hasRealm(assistanceAgent);
			if (super.getRequest().hasData("id")) {
				Integer legId = super.getRequest().getData("leg", Integer.class);
				if (legId == null || legId != 0) {
					Leg leg = this.repository.findLegByLegId(legId);
					status = status && leg != null && !leg.getDraftMode();
				}
			}
			super.getResponse().setAuthorised(status);
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
	public void bind(final Claim claim) {
		super.bindObject(claim, "passengerEmail", "description", "type", "leg", "id");
	}

	@Override
	public void validate(final Claim claim) {
		if (claim.getLeg() == null || claim.getRegistrationMoment() == null)
			return;
		boolean isRegistrationAfterArrival = claim.getRegistrationMoment().after(claim.getLeg().getScheduledArrival());
		super.state(isRegistrationAfterArrival, "leg", "assistanceAgent.claim.form.error.registrationAfterArrival");
	}

	@Override
	public void perform(final Claim claim) {
		this.repository.save(claim);
	}

	@Override
	public void unbind(final Claim claim) {
		SelectChoices choices;
		SelectChoices choices2;
		Dataset dataset;

		choices = SelectChoices.from(ClaimType.class, claim.getType());
		List<Leg> legs = this.repository.findAllLegPublish().stream().filter(leg -> leg.getScheduledArrival().before(claim.getRegistrationMoment())).toList();
		choices2 = SelectChoices.from(legs, "flightNumber", claim.getLeg());

		dataset = super.unbindObject(claim, "registrationMoment", "passengerEmail", "description", "type", "draftMode", "id");
		dataset.put("types", choices);
		dataset.put("leg", choices2.getSelected().getKey());
		dataset.put("legs", choices2);

		super.getResponse().addData(dataset);
	}

}
