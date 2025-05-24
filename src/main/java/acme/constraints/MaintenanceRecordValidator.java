
package acme.constraints;

import java.time.temporal.ChronoUnit;
import java.util.Date;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.client.helpers.MomentHelper;
import acme.entities.maintenance.MaintenanceRecord;
import acme.entities.maintenance.MaintenanceStatus;

public class MaintenanceRecordValidator extends AbstractValidator<ValidMaintenanceRecord, MaintenanceRecord> {

	@Override
	protected void initialise(final ValidMaintenanceRecord annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final MaintenanceRecord maintenanceRecord, final ConstraintValidatorContext context) {
		assert context != null;

		boolean result;

		if (maintenanceRecord == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
		else if (maintenanceRecord.getNextInspectionDue() == null)
			super.state(context, false, "nextInspectionDue", "acme.validation.maintenanceRecord.next-inspection-null.message");
		else {
			Date minimumNextInspectionDue = MomentHelper.deltaFromMoment(maintenanceRecord.getMaintenanceMoment(), 1, ChronoUnit.MINUTES);
			boolean correctNextInspectionDue = MomentHelper.isAfterOrEqual(maintenanceRecord.getNextInspectionDue(), minimumNextInspectionDue);
			super.state(context, correctNextInspectionDue, "nextInspectionDue", "acme.validation.maintenanceRecord.incorrect-dates.message");

			if (!maintenanceRecord.isDraftMode()) {
				boolean statusIsCompleted = maintenanceRecord.getStatus() == MaintenanceStatus.COMPLETED;
				super.state(context, statusIsCompleted, "status", "acme.validation.maintenanceRecord.not-completed.message");
			}
		}

		result = !super.hasErrors(context);
		return result;
	}
}
