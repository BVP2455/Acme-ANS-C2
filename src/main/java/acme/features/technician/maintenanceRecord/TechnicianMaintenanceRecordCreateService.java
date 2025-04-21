
package acme.features.technician.maintenanceRecord;

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.controllers.GuiController;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.entities.aircraft.Aircraft;
import acme.entities.maintenance.MaintenanceRecord;
import acme.entities.maintenance.MaintenanceStatus;
import acme.realms.technician.Technician;

@GuiController
public class TechnicianMaintenanceRecordCreateService extends AbstractGuiService<Technician, MaintenanceRecord> {

	@Autowired
	private TechnicianMaintenanceRecordRepository repository;


	@Override
	public void authorise() {
		boolean status = super.getRequest().getPrincipal().hasRealmOfType(Technician.class);
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		MaintenanceRecord mr;
		Technician technician;
		technician = (Technician) super.getRequest().getPrincipal().getActiveRealm();
		mr = new MaintenanceRecord();
		mr.setDraftMode(true);
		mr.setTechnician(technician);
		super.getBuffer().addData(mr);
	}

	@Override
	public void bind(final MaintenanceRecord maintenanceRecord) {
		Date currentMoment;
		currentMoment = MomentHelper.getCurrentMoment();
		super.bindObject(maintenanceRecord, "nextInspectionDue", "estimatedCost", "notes", "status", "aircraft");
		maintenanceRecord.setMaintenanceMoment(currentMoment);
		// maintenanceRecord.setStatus(MaintenanceStatus.PENDING);
	}

	@Override
	public void validate(final MaintenanceRecord mr) {
		boolean valid;
		valid = super.getRequest().getData("confirmation", boolean.class);
		super.state(valid, "confirmation", "acme.validation.confirmation.message");
	}

	@Override
	public void perform(final MaintenanceRecord mr) {
		this.repository.save(mr);
	}

	@Override
	public void unbind(final MaintenanceRecord maintenanceRecord) {
		maintenanceRecord.setMaintenanceMoment(MomentHelper.getCurrentMoment());
		Dataset dataset;
		SelectChoices choices;
		choices = SelectChoices.from(MaintenanceStatus.class, maintenanceRecord.getStatus());
		SelectChoices aircrafts;
		Collection<Aircraft> aircraftsCollection;
		aircraftsCollection = this.repository.findAllAircrafts();
		aircrafts = SelectChoices.from(aircraftsCollection, "registrationNumber", maintenanceRecord.getAircraft());
		dataset = super.unbindObject(maintenanceRecord, "maintenanceMoment", "status", "nextInspectionDue", "estimatedCost", "notes", "draftMode", "aircraft");
		dataset.put("status", choices.getSelected().getKey());
		dataset.put("statuses", choices);
		dataset.put("aircrafts", aircrafts);
		dataset.put("aircraft", aircrafts.getSelected().getKey());

		super.getResponse().addData(dataset);
	}

}
