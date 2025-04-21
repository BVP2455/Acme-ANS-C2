
package acme.features.technician.maintenanceTask;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.maintenance.MaintenanceRecord;
import acme.entities.maintenance.MaintenanceTask;
import acme.entities.maintenance.Task;
import acme.realms.technician.Technician;

@GuiService
public class TechnicianMaintenanceTaskShowService extends AbstractGuiService<Technician, MaintenanceTask> {

	@Autowired
	private TechnicianMaintenanceTaskRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int id;
		MaintenanceTask mt;

		id = super.getRequest().getData("id", int.class);
		mt = this.repository.findMaintenanceTaskById(id);
		status = mt != null && super.getRequest().getPrincipal().hasRealm(mt.getMaintenanceRecord().getTechnician());

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		MaintenanceTask mt;
		int id;

		id = super.getRequest().getData("id", int.class);
		mt = this.repository.findMaintenanceTaskById(id);

		super.getBuffer().addData(mt);
	}

	@Override
	public void unbind(final MaintenanceTask mt) {
		Dataset dataset;
		SelectChoices choices;
		Collection<Task> tasks;
		MaintenanceRecord mr = mt.getMaintenanceRecord();
		Task task = mt.getTask();

		tasks = this.repository.findAllTasks();
		choices = SelectChoices.from(tasks, "description", mt.getTask());

		dataset = super.unbindObject(mt);
		dataset.put("tasks", choices);
		dataset.put("task", choices.getSelected().getKey());
		dataset.put("description", task.getDescription());
		dataset.put("type", task.getType());
		dataset.put("priority", task.getPriority());
		dataset.put("technician", task.getTechnician().getLicenseNumber());
		dataset.put("aircraft", mr.getAircraft().getRegistrationNumber());
		dataset.put("maintenanceRecord", mr.getId());
		super.getResponse().addData(dataset);

		boolean draftMode;
		draftMode = mr.isDraftMode();

		super.getResponse().addGlobal("draftMode", draftMode);
	}

}
