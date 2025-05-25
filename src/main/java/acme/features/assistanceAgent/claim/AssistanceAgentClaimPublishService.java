
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
import acme.realms.assistanceAgents.AssistanceAgent;

@GuiService
public class AssistanceAgentClaimPublishService extends AbstractGuiService<AssistanceAgent, Claim> {

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
		claim.setDraftMode(false);
		this.repository.save(claim);
	}

	@Override
	public void unbind(final Claim claim) {
		assert claim != null;
		Dataset dataset;

		SelectChoices types = SelectChoices.from(ClaimType.class, claim.getType());
		Collection<Leg> allLegs = this.repository.findAllLegPublish();
		Collection<Leg> availableLegs = allLegs.stream().filter(leg -> leg.getScheduledArrival().before(claim.getRegistrationMoment())).toList();

		dataset = super.unbindObject(claim, "registrationMoment", "passengerEmail", "description", "type", "id", "leg");

		if (!claim.isDraftMode()) {
			SelectChoices legChoices = SelectChoices.from(allLegs, "flightNumber", claim.getLeg());
			dataset.put("leg", legChoices.getSelected() != null ? legChoices.getSelected().getKey() : "0");
			dataset.put("legs", legChoices);
		} else {
			boolean legStillValid = availableLegs.contains(claim.getLeg());

			if (!legStillValid) {
				SelectChoices choices = SelectChoices.from(availableLegs, "flightNumber", availableLegs.stream().findFirst().orElse(null));
				dataset.put("leg", choices.getSelected() != null ? choices.getSelected().getKey() : "0");
				dataset.put("legs", choices);
			} else {
				SelectChoices choices = SelectChoices.from(availableLegs, "flightNumber", claim.getLeg());
				dataset.put("leg", choices.getSelected() != null ? choices.getSelected().getKey() : "0");
				dataset.put("legs", choices);
			}
		}

		dataset.put("types", types);
		dataset.put("readonly", false);

		super.getResponse().addData(dataset);
	}

}
