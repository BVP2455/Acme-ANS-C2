
package acme.features.technician.task;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.maintenance.Task;
import acme.realms.technician.Technician;

@GuiService
public class TechnicianTaskListService extends AbstractGuiService<Technician, Task> {

	@Autowired
	private TechnicianTaskRepository repository;


	@Override
	public void authorise() {
		boolean status = super.getRequest().getPrincipal().hasRealmOfType(Technician.class);
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Collection<Task> tasks;
		int technicianId;
		technicianId = super.getRequest().getPrincipal().getActiveRealm().getId();
		tasks = this.repository.findAllTasksOfTechnician(technicianId);

		super.getBuffer().addData(tasks);
	}

	@Override
	public void unbind(final Task task) {
		Dataset dataset;

		dataset = super.unbindObject(task, "type", "description", "priority", "estimatedDuration", "draftMode");
		super.addPayload(dataset, task);
		super.getResponse().addData(dataset);
	}
}
