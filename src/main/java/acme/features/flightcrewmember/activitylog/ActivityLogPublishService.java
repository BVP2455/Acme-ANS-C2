
package acme.features.flightcrewmember.activitylog;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.activitylog.ActivityLog;
import acme.entities.flightassignment.FlightAssignment;
import acme.realms.flightcrewmember.FlightCrewMember;

@GuiService
public class ActivityLogPublishService extends AbstractGuiService<FlightCrewMember, ActivityLog> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ActivityLogRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {

		boolean status;
		int acitivityLogId;
		ActivityLog activityLog;
		int flightCrewMemberId;

		acitivityLogId = super.getRequest().getData("id", int.class);
		activityLog = this.repository.findActivityLogById(acitivityLogId);
		flightCrewMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();

		status = activityLog != null && activityLog.isDraftMode() && activityLog.getActivityLogAssignment() != null && activityLog.getActivityLogAssignment().getFlightCrewMember().getId() == flightCrewMemberId;

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {

		ActivityLog activityLog;
		int id;

		id = super.getRequest().getData("id", int.class);
		activityLog = this.repository.findActivityLogById(id);

		super.getBuffer().addData(activityLog);
	}

	@Override
	public void bind(final ActivityLog activityLog) {
		super.bindObject(activityLog, "typeOfIncident", "description", "severityLevel");
	}

	@Override
	public void validate(final ActivityLog activityLog) {

		FlightAssignment flightAssignment;

		flightAssignment = activityLog.getActivityLogAssignment();

		if (flightAssignment.isDraftMode())
			super.state(false, "*", "acme.validation.activitylog.flightassignment.publish.message");
	}

	@Override
	public void perform(final ActivityLog activityLog) {
		activityLog.setDraftMode(false);
		this.repository.save(activityLog);
	}

	@Override
	public void unbind(final ActivityLog activityLog) {

		Dataset dataset;

		dataset = super.unbindObject(activityLog, "registrationMoment", "typeOfIncident", "description", "severityLevel", "draftMode", "activityLogAssignment");

		dataset.put("flightAssignment", activityLog.getActivityLogAssignment());
		dataset.put("masterId", activityLog.getActivityLogAssignment().getId());
		dataset.put("draftMode", activityLog.getActivityLogAssignment().isDraftMode());

		super.getResponse().addData(dataset);
	}
}
