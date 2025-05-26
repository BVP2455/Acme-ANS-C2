
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.entities.maintenance.MaintenanceTask;
import acme.entities.maintenance.MaintenanceTaskRepository;

@Validator
public class MaintenanceTaskValidator extends AbstractValidator<ValidMaintenanceTask, MaintenanceTask> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private MaintenanceTaskRepository repository;

	// ConstraintValidator interface ------------------------------------------


	@Override
	protected void initialise(final ValidMaintenanceTask annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final MaintenanceTask maintenanceTask, final ConstraintValidatorContext context) {
		assert context != null;

		boolean result;

		if (maintenanceTask == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
		else if (maintenanceTask.getMaintenanceRecord() == null)
			super.state(context, false, "maintenanceRecord", "acme.validation.maintenanceTask.null-maintenanceRecord.message");
		else if (maintenanceTask.getTask() == null)
			super.state(context, false, "task", "acme.validation.maintenanceTask.null-task.message");
		else {
			long count = this.repository.countByTaskIdAndMaintenanceRecordId(maintenanceTask.getTask().getId(), maintenanceTask.getMaintenanceRecord().getId());

			boolean unique = count == 0 || count == 1;
			super.state(context, unique, "*", "acme.validation.maintenanceTask.duplicated-combination.message");

			boolean mrDraft = maintenanceTask.getMaintenanceRecord().isDraftMode();
			boolean taskDraft = maintenanceTask.getTask().isDraftMode();

			boolean invalidCombination = !mrDraft && taskDraft;
			super.state(context, !invalidCombination, "*", "acme.validation.maintenanceTask.task-in-draft-in-published-record.message");

			if (mrDraft && taskDraft) {
				int technicianMR = maintenanceTask.getMaintenanceRecord().getTechnician().getId();
				int technicianTask = maintenanceTask.getTask().getTechnician().getId();
				boolean sameTechnician = technicianMR == technicianTask;
				super.state(context, sameTechnician, "*", "acme.validation.maintenanceTask.different-technicians.message");
			}
		}

		result = !super.hasErrors(context);
		return result;
	}
}
