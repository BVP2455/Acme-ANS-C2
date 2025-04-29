
package acme.features.technician.maintenanceRecord;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircraft.Aircraft;
import acme.entities.maintenance.MaintenanceRecord;
import acme.entities.maintenance.MaintenanceStatus;
import acme.entities.maintenance.Task;
import acme.realms.technician.Technician;

@GuiService
public class TechnicianMaintenanceRecordShowService extends AbstractGuiService<Technician, MaintenanceRecord> {

	@Autowired
	private TechnicianMaintenanceRecordRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int id;
		MaintenanceRecord mr;
		Technician technician;
		id = super.getRequest().getData("id", int.class);
		mr = this.repository.findMaintenanceRecordById(id);
		if (mr.isDraftMode()) {
			technician = mr == null ? null : mr.getTechnician();
			status = super.getRequest().getPrincipal().hasRealm(technician);
		} else
			status = super.getRequest().getPrincipal().hasRealmOfType(Technician.class) && mr != null;
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		MaintenanceRecord mr;
		int id;
		id = super.getRequest().getData("id", int.class);

		mr = this.repository.findMaintenanceRecordById(id);
		super.getBuffer().addData(mr);
	}

	@Override
	public void unbind(final MaintenanceRecord mr) {
		Dataset dataset;
		SelectChoices statusChoices;
		statusChoices = SelectChoices.from(MaintenanceStatus.class, mr.getStatus());
		SelectChoices aircraftChoices;
		Collection<Aircraft> aircrafts;
		aircrafts = this.repository.findAllAircrafts();
		aircraftChoices = SelectChoices.from(aircrafts, "registrationNumber", mr.getAircraft());
		dataset = super.unbindObject(mr, "maintenanceMoment", "status", "nextInspectionDue", "estimatedCost", "notes", "draftMode");
		dataset.put("statuses", statusChoices);
		dataset.put("aircrafts", aircraftChoices);
		dataset.put("aircraft", aircraftChoices.getSelected().getKey());
		super.getResponse().addData(dataset);

		boolean canBePublished;
		Collection<Task> tasksOfMr = this.repository.findTasksInMaintenanceRecord(mr.getId());
		canBePublished = mr.isDraftMode() && !tasksOfMr.isEmpty() && tasksOfMr.stream().allMatch(task -> !task.isDraftMode());
		super.getResponse().addGlobal("canBePublished", canBePublished);

	}

}
