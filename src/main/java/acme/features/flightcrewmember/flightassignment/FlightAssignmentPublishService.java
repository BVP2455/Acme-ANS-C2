
package acme.features.flightcrewmember.flightassignment;

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flightassignment.CurrentStatus;
import acme.entities.flightassignment.FlightAssignment;
import acme.entities.flightassignment.FlightCrewDuty;
import acme.entities.legs.Leg;
import acme.realms.flightcrewmember.AvaiabilityStatus;
import acme.realms.flightcrewmember.FlightCrewMember;

@GuiService
public class FlightAssignmentPublishService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	// Internal state ---------------------------------------------------------
	@Autowired
	private FlightAssignmentRepository repository;


	// AbstractGuiService interface -------------------------------------------
	@Override
	public void authorise() {

		boolean status;
		int masterId;
		FlightAssignment flightAssignment;

		masterId = super.getRequest().getData("id", int.class);
		flightAssignment = this.repository.findFlightAssignmentById(masterId);

		status = flightAssignment.isDraftMode();

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		FlightAssignment flightAssignment;

		flightAssignment = new FlightAssignment();

		super.getBuffer().addData(flightAssignment);
	}

	@Override
	public void bind(final FlightAssignment flightAssignment) {
		super.bindObject(flightAssignment, "duty", "lastUpdateMoment", "currentStatus", "remarks", "flightCrewMember", "leg");
	}

	@Override
	public void validate(final FlightAssignment flightAssignment) {

		int flightCrewMemberId;
		int legId;

		boolean completedLeg;
		boolean availableMember;
		boolean hasSimultaneousLegs;
		boolean hasPilot;
		boolean hasCopilot;

		Date departure;
		Date arrival;

		Collection<Leg> simultaneousLegs;
		Collection<FlightAssignment> pilotAssignments;
		Collection<FlightAssignment> copilotAssignments;
		Leg leg;

		flightCrewMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		leg = flightAssignment.getLeg();
		legId = leg.getId();

		completedLeg = MomentHelper.isBefore(flightAssignment.getLeg().getScheduledArrival(), MomentHelper.getCurrentMoment());

		//member available
		availableMember = this.repository.findFlightCrewMemberById(flightCrewMemberId).getAvailabilityStatus().equals(AvaiabilityStatus.AVAILABLE);

		hasSimultaneousLegs = false;
		departure = flightAssignment.getLeg().getScheduledDeparture();
		arrival = flightAssignment.getLeg().getScheduledArrival();
		simultaneousLegs = this.repository.findSimultaneousLegsByMemberId(departure, arrival, legId, flightCrewMemberId);

		if (simultaneousLegs.isEmpty())
			hasSimultaneousLegs = true;

		pilotAssignments = this.repository.findFlightAssignmentByLegAndDuty(leg, FlightCrewDuty.PILOT);
		copilotAssignments = this.repository.findFlightAssignmentByLegAndDuty(leg, FlightCrewDuty.COPILOT);

		hasPilot = true;
		hasCopilot = true;

		//maximo 1 piloto y 1 co-piloto cada leg
		if (flightAssignment.getDuty().equals(FlightCrewDuty.PILOT) && pilotAssignments.size() + 1 >= 2)
			hasPilot = false;
		if (flightAssignment.getDuty().equals(FlightCrewDuty.COPILOT) && copilotAssignments.size() + 1 >= 2)
			hasCopilot = false;

		if (!this.getBuffer().getErrors().hasErrors("publish")) {
			super.state(!completedLeg, "flightAssignmentLeg", "acme.validation.flightassignment.leg.completed.message", flightAssignment);
			super.state(availableMember, "flightAssignmentCrewMember", "acme.validation.flightassignment.flightcrewmember.available.message", flightAssignment);
			super.state(hasSimultaneousLegs, "flightAssignmentLeg", "acme.validation.flightassignment.leg.overlap.message", flightAssignment);
			super.state(hasPilot, "duty", "acme.validation.flightassignment.duty.pilot.message", flightAssignment);
			super.state(hasCopilot, "duty", "acme.validation.flightassignment.duty.copilot.message", flightAssignment);
		}
	}

	@Override
	public void perform(final FlightAssignment flightAssignment) {
		flightAssignment.setDraftMode(false);
		this.repository.save(flightAssignment);
	}

	@Override
	public void unbind(final FlightAssignment flightAssignment) {
		Dataset dataset;
		SelectChoices dutyChoice;
		SelectChoices currentStatusChoice;

		SelectChoices legChoice;
		Collection<Leg> legs;

		Collection<FlightCrewMember> flightCrewMembers;

		dutyChoice = SelectChoices.from(FlightCrewDuty.class, flightAssignment.getDuty());
		currentStatusChoice = SelectChoices.from(CurrentStatus.class, flightAssignment.getCurrentStatus());

		legs = this.repository.findAllLegs();
		legChoice = SelectChoices.from(legs, "id", flightAssignment.getLeg());

		flightCrewMembers = this.repository.findAllFlightCrewMembers();

		dataset = super.unbindObject(flightAssignment, "duty", "lastUpdateMoment", "currentStatus", "remarks", "draftMode", "leg", "flightCrewMember");
		dataset.put("dutyChoice", dutyChoice);
		dataset.put("currentStatusChoice", currentStatusChoice);
		dataset.put("legChoice", legChoice);
		dataset.put("flightCrewMemberChoice", flightAssignment.getFlightCrewMember());

		super.getResponse().addData(dataset);
	}
}
