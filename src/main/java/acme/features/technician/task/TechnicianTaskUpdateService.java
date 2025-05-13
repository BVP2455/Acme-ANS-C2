
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
public class TechnicianTaskUpdateService extends AbstractGuiService<Technician, Task> {

	@Autowired
	private TechnicianTaskRepository repository;


	@Override
	public void authorise() {
		boolean status = false;
		int id;
		Task task;
		Technician technician;

		id = super.getRequest().getData("id", int.class);
		task = this.repository.findTaskById(id);
		technician = task == null ? null : task.getTechnician();

		if (task != null && task.isDraftMode() && super.getRequest().getPrincipal().hasRealm(technician)) {

			status = true;

			if (super.getRequest().getMethod().equals("POST")) {
				String taskTypeInput = super.getRequest().getData("type", String.class);
				boolean taskTypeValid = false;

				if (taskTypeInput != null) {
					String trimmedInput = taskTypeInput.trim();
					if (trimmedInput.equals("0"))
						taskTypeValid = true;
					else
						for (TaskType tt : TaskType.values())
							if (tt.name().equalsIgnoreCase(trimmedInput)) {
								taskTypeValid = true;
								break;
							}
				}

				status = status && taskTypeValid;
			}
		}
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
	public void bind(final Task task) {
		super.bindObject(task, "type", "description", "priority", "estimatedDuration");

	}

	@Override
	public void validate(final Task task) {
		boolean confirmation;
		confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");
	}

	@Override
	public void perform(final Task task) {
		this.repository.save(task);
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
	}

}
