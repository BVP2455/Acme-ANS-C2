
package acme.features.assistanceAgent.claim;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claim.Claim;
import acme.entities.claim.ClaimType;
import acme.entities.leg.Leg;
import acme.realms.assistanceAgents.AssistanceAgent;

@GuiService
public class AssistanceAgentClaimCreateService extends AbstractGuiService<AssistanceAgent, Claim> {

	@Autowired
	private ClaimRepository repository;


	@Override
	public void authorise() {
		boolean status = super.getRequest().getPrincipal().hasRealmOfType(AssistanceAgent.class);

		super.getResponse().setAuthorised(status);

		if (status && super.getRequest().getMethod().equals("POST")) {

			Integer legId = super.getRequest().getData("leg", Integer.class);
			Leg leg = super.getRequest().getData("leg", Leg.class);

			Collection<Leg> legs = this.repository.findAllLeg().stream().collect(Collectors.toList());
			Collection<Leg> legsAvaiables = legs.stream().filter(l -> l.getScheduledDeparture().after(MomentHelper.getCurrentMoment()) && !l.getDraftMode()).collect(Collectors.toList());

			if (legId != 0 && !legsAvaiables.contains(leg))
				status = false;

			if (leg != null && leg.getDraftMode())
				status = false;

			super.getResponse().setAuthorised(status);
		}
	}

	@Override
	public void load() {
		AssistanceAgent assistanceAgent = (AssistanceAgent) super.getRequest().getPrincipal().getActiveRealm();
		Claim claim;

		claim = new Claim();
		claim.setRegistrationMoment(MomentHelper.getCurrentMoment());
		claim.setPassengerEmail("");
		claim.setDescription("");
		claim.setType(ClaimType.FLIGHT_ISSUES);
		claim.setAssistanceAgent(assistanceAgent);

		super.getBuffer().addData(claim);
	}

	@Override
	public void bind(final Claim claim) {
		super.bindObject(claim, "registrationMoment", "passengerEmail", "description", "type", "id", "leg");

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
		claim.setRegistrationMoment(MomentHelper.getCurrentMoment());
		claim.setDraftMode(true);

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

		dataset = super.unbindObject(claim, "registrationMoment", "passengerEmail", "description", "type", "id", "leg");
		dataset.put("readonly", false);
		dataset.put("types", choices);
		dataset.put("legs", choices2);

		super.getResponse().addData(dataset);
	}

}
