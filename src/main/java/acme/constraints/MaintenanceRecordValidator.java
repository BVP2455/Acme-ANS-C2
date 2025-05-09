
package acme.constraints;

import java.time.temporal.ChronoUnit;
import java.util.Date;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.client.helpers.MomentHelper;
import acme.entities.maintenance.MaintenanceRecord;

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
			Date minimumNextInspectionDue;
			boolean correctNextInspectionDue;

			minimumNextInspectionDue = MomentHelper.deltaFromMoment(maintenanceRecord.getMaintenanceMoment(), 1, ChronoUnit.MINUTES);
			correctNextInspectionDue = MomentHelper.isAfterOrEqual(maintenanceRecord.getNextInspectionDue(), minimumNextInspectionDue);

			super.state(context, correctNextInspectionDue, "nextInspectionDue", "acme.validation.maintenanceRecord.incorrect-dates.message");
		}
		result = !super.hasErrors(context);

		return result;
	}

}
