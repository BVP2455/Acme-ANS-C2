
package acme.features.technician.task;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.maintenance.Task;
import acme.entities.maintenance.TaskType;
import acme.realms.technician.Technician;

@GuiService
public class TechnicianTaskShowService extends AbstractGuiService<Technician, Task> {

	@Autowired
	private TechnicianTaskRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int id;
		Task task;
		Technician technician;
		id = super.getRequest().getData("id", int.class);
		task = this.repository.findTaskById(id);
		technician = task == null ? null : task.getTechnician();
		status = super.getRequest().getPrincipal().hasRealm(technician);
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Task task;
		int id;
		id = super.getRequest().getData("id", int.class);

		task = this.repository.findTaskById(id);
		super.getBuffer().addData(task);
	}

	@Override
	public void unbind(final Task task) {
		Dataset dataset;
		SelectChoices taskChoices;
		taskChoices = SelectChoices.from(TaskType.class, task.getType());

		dataset = super.unbindObject(task, "type", "description", "priority", "estimatedDuration", "draftMode");
		dataset.put("type", taskChoices.getSelected().getKey());
		dataset.put("types", taskChoices);
		super.getResponse().addData(dataset);

		//		boolean canBePublished;
		//		canBePublished = task.isDraftMode();
		//		super.getResponse().addGlobal("canBePublished", canBePublished);

	}

}
