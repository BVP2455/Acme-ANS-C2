
package acme.features.assistanceAgent.claim;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claim.Claim;
import acme.entities.claim.ClaimType;
import acme.entities.leg.Leg;
import acme.entities.trackingLog.TrackingLog;
import acme.entities.trackingLog.TrackingLogStatus;
import acme.features.assistanceAgent.trackingLog.AssistanceAgentTrackingLogRepository;
import acme.realms.assistanceAgents.AssistanceAgent;

@GuiService
public class AssistanceAgentClaimCreateService extends AbstractGuiService<AssistanceAgent, Claim> {

	@Autowired
	private ClaimRepository							repository;

	@Autowired
	private AssistanceAgentTrackingLogRepository	repositoryTrackingLogs;


	@Override
	public void authorise() {
		boolean status = super.getRequest().getPrincipal().hasRealmOfType(AssistanceAgent.class);
		if (super.getRequest().hasData("id")) {
			Integer legId = super.getRequest().getData("leg", Integer.class);
			if (legId == null || legId != 0) {
				Leg leg = this.repository.findLegByLegId(legId);
				status = status && leg != null && !leg.getDraftMode();
			}
		}
		super.getResponse().setAuthorised(status);
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
		super.bindObject(claim, "passengerEmail", "description", "type", "id", "leg");

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
		TrackingLog initialLog = new TrackingLog();
		initialLog.setClaim(claim);
		initialLog.setRegistrationMoment(MomentHelper.getCurrentMoment());
		initialLog.setLastUpdateMoment(MomentHelper.getCurrentMoment());
		initialLog.setStep("Claim registered");
		initialLog.setResolutionPercentage(0.0);
		initialLog.setStatus(TrackingLogStatus.PENDING);
		initialLog.setDraftMode(false);
		this.repositoryTrackingLogs.save(initialLog);
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
