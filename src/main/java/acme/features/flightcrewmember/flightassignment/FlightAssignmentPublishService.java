
package acme.features.flightcrewmember.flightassignment;

import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flightassignment.CurrentStatus;
import acme.entities.flightassignment.FlightAssignment;
import acme.entities.flightassignment.FlightCrewDuty;
import acme.entities.leg.Leg;
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
		int flightCrewMemberId;
		FlightAssignment flightAssignment;

		masterId = super.getRequest().getData("id", int.class);
		flightAssignment = this.repository.findFlightAssignmentById(masterId);
		flightCrewMemberId = flightAssignment == null ? null : super.getRequest().getPrincipal().getActiveRealm().getId();

		status = flightAssignment != null && flightAssignment.getFlightCrewMember().getId() == flightCrewMemberId && flightAssignment.isDraftMode();

		super.getResponse().setAuthorised(status);

		if (status && super.getRequest().getMethod().equals("POST")) {

			Integer legId = super.getRequest().getData("leg", Integer.class);
			Leg leg = super.getRequest().getData("leg", Leg.class);

			Collection<Leg> legs = this.repository.findAllLegs().stream().collect(Collectors.toList());
			Collection<Leg> legsAvaiables = legs.stream().filter(f -> f.getScheduledDeparture().after(MomentHelper.getCurrentMoment()) && !f.getDraftMode()).collect(Collectors.toList());

			if (legId != 0 && !legsAvaiables.contains(leg))
				status = false;

			if (leg != null && leg.getDraftMode())
				status = false;
		}
	}

	@Override
	public void load() {
		FlightAssignment flightAssignment;

		flightAssignment = new FlightAssignment();

		super.getBuffer().addData(flightAssignment);
	}

	@Override
	public void bind(final FlightAssignment flightAssignment) {
		super.bindObject(flightAssignment, "duty", "currentStatus", "remarks", "leg");
	}

	@Override
	public void validate(final FlightAssignment flightAssignment) {

		int flightCrewMemberId;

		boolean completedLeg;

		boolean availableMember;

		boolean overlappedLeg;
		Date departure;
		Date arrival;
		Collection<FlightAssignment> overlappedLegs;

		int pilots;
		int copilots;

		flightCrewMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();

		//validation1: el leg ya ha ocurrido

		completedLeg = MomentHelper.isBefore(flightAssignment.getLeg().getScheduledArrival(), MomentHelper.getCurrentMoment());
		super.state(!completedLeg, "*", "acme.validation.flightassignment.leg.completed.message");

		//validation 2: member available

		availableMember = this.repository.findFlightCrewMemberById(flightCrewMemberId).getAvailabilityStatus().equals(AvaiabilityStatus.AVAILABLE);
		super.state(availableMember, "*", "acme.validation.flightassignment.flightcrewmember.available.message");

		//validation 3: overlapped legs

		overlappedLeg = false;
		departure = flightAssignment.getLeg().getScheduledDeparture();
		arrival = flightAssignment.getLeg().getScheduledArrival();

		overlappedLegs = this.repository.findflightAssignmentsWithOverlappedLegsByMemberId(departure, arrival, flightCrewMemberId);

		if (overlappedLegs.isEmpty())
			overlappedLeg = true;

		super.state(overlappedLeg, "*", "acme.validation.flightassignment.leg.overlap.message");

		//validation 4: maximum 1 pilot and 1 copilot each leg

		if (flightAssignment.getDuty() != null && flightAssignment.getLeg() != null)
			if (flightAssignment.getDuty().equals(FlightCrewDuty.PILOT)) {
				pilots = this.repository.hasDutyAssigned(flightAssignment.getLeg().getId(), flightAssignment.getDuty(), flightAssignment.getId());
				super.state(pilots == 0, "*", "acme.validation.flightassignment.duty.pilot.message");

			} else if (flightAssignment.getDuty().equals(FlightCrewDuty.COPILOT)) {
				copilots = this.repository.hasDutyAssigned(flightAssignment.getLeg().getId(), flightAssignment.getDuty(), flightAssignment.getId());
				super.state(copilots == 0, "*", "acme.validation.flightassignment.duty.copilot.message");
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

		SelectChoices flightCrewMemberChoice;
		Collection<FlightCrewMember> flightCrewMembers;

		dutyChoice = SelectChoices.from(FlightCrewDuty.class, flightAssignment.getDuty());
		currentStatusChoice = SelectChoices.from(CurrentStatus.class, flightAssignment.getCurrentStatus());

		legs = this.repository.findAllLegs();
		legChoice = SelectChoices.from(legs, "flightNumber", flightAssignment.getLeg());

		flightCrewMembers = this.repository.findAllFlightCrewMembers();
		flightCrewMemberChoice = SelectChoices.from(flightCrewMembers, "employeeCode", flightAssignment.getFlightCrewMember());

		dataset = super.unbindObject(flightAssignment, "duty", "lastUpdateMoment", "currentStatus", "remarks", "leg", "draftMode");
		dataset.put("dutyChoice", dutyChoice);
		dataset.put("currentStatusChoice", currentStatusChoice);
		dataset.put("legChoice", legChoice);
		dataset.put("flightCrewMemberChoice", flightCrewMemberChoice);

		super.getResponse().addData(dataset);
	}
}
