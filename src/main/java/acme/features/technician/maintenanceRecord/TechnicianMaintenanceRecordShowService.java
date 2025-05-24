
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
		boolean status = false;
		int id;
		MaintenanceRecord mr;
		int technicianId;
		id = super.getRequest().getData("id", int.class);
		mr = this.repository.findMaintenanceRecordById(id);
		if (mr == null)
			status = false;
		else if (mr.isDraftMode()) {
			technicianId = super.getRequest().getPrincipal().getActiveRealm().getId();
			status = technicianId == mr.getTechnician().getId();
		} else
			status = super.getRequest().getPrincipal().hasRealmOfType(Technician.class);
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
		canBePublished = mr.getStatus().equals(MaintenanceStatus.COMPLETED) && mr.isDraftMode() && !tasksOfMr.isEmpty() && tasksOfMr.stream().allMatch(task -> !task.isDraftMode());
		super.getResponse().addGlobal("canBePublished", canBePublished);

	}

}
