
package acme.features.flightcrewmember.flightassignment;

import java.util.Collection;
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
import acme.realms.flightcrewmember.FlightCrewMember;

@GuiService
public class FlightAssignmentCreateService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private FlightAssignmentRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {

		boolean status = super.getRequest().getPrincipal().hasRealmOfType(FlightCrewMember.class);

		super.getResponse().setAuthorised(status);

		if (status && super.getRequest().getMethod().equals("POST")) {

			Integer legId = super.getRequest().getData("leg", Integer.class);
			Leg leg = super.getRequest().getData("leg", Leg.class);

			Collection<Leg> legs = this.repository.findAllLegs().stream().collect(Collectors.toList());
			Collection<Leg> legsAvaiables = legs.stream().filter(l -> !l.getDraftMode()).collect(Collectors.toList());

			if (legId != 0 && !legsAvaiables.contains(leg))
				status = false;

			if (leg != null && leg.getDraftMode())
				status = false;

			super.getResponse().setAuthorised(status);
		}

	}

	@Override
	public void load() {
		FlightAssignment flightAssignment;
		FlightCrewMember flightCrewMember = (FlightCrewMember) super.getRequest().getPrincipal().getActiveRealm();

		flightAssignment = new FlightAssignment();
		flightAssignment.setFlightCrewMember(flightCrewMember);
		flightAssignment.setLastUpdateMoment(MomentHelper.getCurrentMoment());
		flightAssignment.setDraftMode(true);
		super.getBuffer().addData(flightAssignment);
	}

	@Override
	public void bind(final FlightAssignment flightAssignment) {

		super.bindObject(flightAssignment, "duty", "currentStatus", "remarks", "leg");
	}

	@Override
	public void validate(final FlightAssignment flightAssignment) {
		;
	}

	@Override
	public void perform(final FlightAssignment flightAssignment) {
		assert flightAssignment != null;

		this.repository.save(flightAssignment);
	}

	@Override
	public void unbind(final FlightAssignment flightAssignment) {
		Dataset dataset;

		SelectChoices dutyChoice;
		SelectChoices currentStatusChoice;

		SelectChoices flightCrewMemberChoice;
		Collection<FlightCrewMember> flightCrewMembers;

		SelectChoices legChoice;
		Collection<Leg> legs;

		dutyChoice = SelectChoices.from(FlightCrewDuty.class, flightAssignment.getDuty());
		currentStatusChoice = SelectChoices.from(CurrentStatus.class, flightAssignment.getCurrentStatus());

		legs = this.repository.findAllLegs();
		legChoice = SelectChoices.from(legs, "flightNumber", flightAssignment.getLeg());

		flightCrewMembers = this.repository.findAllFlightCrewMembers();
		flightCrewMemberChoice = SelectChoices.from(flightCrewMembers, "employeeCode", flightAssignment.getFlightCrewMember());

		dataset = super.unbindObject(flightAssignment, "duty", "currentStatus", "remarks", "draftMode", "leg");
		dataset.put("lastUpdateMoment", flightAssignment.getLastUpdateMoment());
		dataset.put("dutyChoice", dutyChoice);
		dataset.put("currentStatusChoice", currentStatusChoice);
		dataset.put("legChoice", legChoice);
		dataset.put("flightCrewMemberChoice", flightCrewMemberChoice);

		super.getResponse().addData(dataset);
	}

}
