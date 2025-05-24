
package acme.features.technician.task;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.controllers.GuiController;
import acme.client.services.AbstractGuiService;
import acme.entities.maintenance.Task;
import acme.entities.maintenance.TaskType;
import acme.realms.technician.Technician;

@GuiController
public class TechnicianTaskCreateService extends AbstractGuiService<Technician, Task> {

	@Autowired
	private TechnicianTaskRepository repository;


	@Override
	public void authorise() {
		boolean status = false;

		if (super.getRequest().getPrincipal().hasRealmOfType(Technician.class)) {
			status = true;
			if (super.getRequest().getMethod().equals("GET"))
				status = !super.getRequest().hasData("id", Integer.class);
			if (super.getRequest().getMethod().equals("POST")) {
				int id = super.getRequest().getData("id", int.class);
				boolean validId = id == 0;

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

				status = status && taskTypeValid && validId;
			}
		}
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Task task;
		Technician technician;
		technician = (Technician) super.getRequest().getPrincipal().getActiveRealm();
		task = new Task();
		task.setDraftMode(true);
		task.setTechnician(technician);
		super.getBuffer().addData(task);
	}

	@Override
	public void bind(final Task task) {
		super.bindObject(task, "type", "description", "priority", "estimatedDuration");
	}

	@Override
	public void validate(final Task task) {
		boolean valid;
		valid = super.getRequest().getData("confirmation", boolean.class);
		super.state(valid, "confirmation", "acme.validation.confirmation.message");
	}

	@Override
	public void perform(final Task task) {
		this.repository.save(task);
	}

	@Override
	public void unbind(final Task task) {
		Dataset dataset;
		SelectChoices choices;
		choices = SelectChoices.from(TaskType.class, task.getType());

		dataset = super.unbindObject(task, "type", "description", "priority", "estimatedDuration", "draftMode");
		dataset.put("type", choices.getSelected().getKey());
		dataset.put("types", choices);

		super.getResponse().addData(dataset);
	}

}
