
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
		boolean status = false;

		if (super.getRequest().getPrincipal().hasRealmOfType(Technician.class)) {
			status = true;

			if (super.getRequest().getMethod().equals("POST")) {
				boolean aircraftValid;
				boolean maintenanceStatusValid;

				int aircraftId = super.getRequest().getData("aircraft", int.class);
				String statusInput = super.getRequest().getData("status", String.class);

				if (aircraftId == 0)
					aircraftValid = true;
				else {
					Aircraft existingAircraft = this.repository.findAircraftById(aircraftId);
					aircraftValid = existingAircraft != null;
				}

				if (statusInput.trim().equals("0"))
					maintenanceStatusValid = true;
				else {
					maintenanceStatusValid = false;
					for (MaintenanceStatus ms : MaintenanceStatus.values())
						if (ms.name().equalsIgnoreCase(statusInput)) {
							maintenanceStatusValid = true;
							break;
						}
				}

				status = status && aircraftValid && maintenanceStatusValid;
			}
		}

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		MaintenanceRecord mr;
		Technician technician;
		Date currentMoment;
		currentMoment = MomentHelper.getCurrentMoment();
		technician = (Technician) super.getRequest().getPrincipal().getActiveRealm();
		mr = new MaintenanceRecord();
		mr.setDraftMode(true);
		mr.setTechnician(technician);
		mr.setMaintenanceMoment(currentMoment);
		super.getBuffer().addData(mr);
	}

	@Override
	public void bind(final MaintenanceRecord maintenanceRecord) {
		super.bindObject(maintenanceRecord, "nextInspectionDue", "estimatedCost", "notes", "status", "aircraft");
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
		dataset = super.unbindObject(maintenanceRecord, "status", "nextInspectionDue", "estimatedCost", "notes", "draftMode", "aircraft");
		dataset.put("status", choices.getSelected().getKey());
		dataset.put("statuses", choices);
		dataset.put("aircrafts", aircrafts);
		dataset.put("aircraft", aircrafts.getSelected().getKey());
		dataset.put("maintenanceMoment", maintenanceRecord.getMaintenanceMoment());
		super.getResponse().addData(dataset);
	}

}
