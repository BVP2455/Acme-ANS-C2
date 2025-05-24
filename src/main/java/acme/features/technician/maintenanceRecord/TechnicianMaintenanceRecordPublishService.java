
package acme.features.technician.maintenanceRecord;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircraft.Aircraft;
import acme.entities.maintenance.MaintenanceRecord;
import acme.entities.maintenance.MaintenanceStatus;
import acme.entities.maintenance.Task;
import acme.realms.technician.Technician;

@GuiService
public class TechnicianMaintenanceRecordPublishService extends AbstractGuiService<Technician, MaintenanceRecord> {

	@Autowired
	private TechnicianMaintenanceRecordRepository repository;


	@Override
	public void authorise() {
		boolean status = false;
		int id;
		MaintenanceRecord mr;
		int technicianId;

		if (super.getRequest().hasData("id", Integer.class) && super.getRequest().getMethod().equals("POST")) {
			id = super.getRequest().getData("id", int.class);
			mr = this.repository.findMaintenanceRecordById(id);
			if (mr != null) {
				technicianId = super.getRequest().getPrincipal().getActiveRealm().getId();
				if (mr.isDraftMode()) {
					status = technicianId == mr.getTechnician().getId();
					boolean aircraftValid;
					boolean maintenanceStatusValid;
					int aircraftId = super.getRequest().getData("aircraft", int.class);
					String statusInput = super.getRequest().getData("status", String.class);
					if (aircraftId == 0)
						aircraftValid = true;
					else {
						Aircraft existingAircraft = this.repository.findAircraftById(aircraftId);
						aircraftValid = existingAircraft != null;
					}

					if (statusInput.trim().equals("0"))
						maintenanceStatusValid = true;
					else {
						maintenanceStatusValid = false;
						for (MaintenanceStatus ms : MaintenanceStatus.values())
							if (ms.name().equalsIgnoreCase(statusInput)) {
								maintenanceStatusValid = true;
								break;
							}
					}
					status = status && aircraftValid && maintenanceStatusValid;
				}
			}
		}
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
	public void bind(final MaintenanceRecord mr) {
	}

	@Override
	public void validate(final MaintenanceRecord mr) {

		Collection<Task> tasksOfMr = this.repository.findTasksInMaintenanceRecord(mr.getId());

		if (mr.getEstimatedCost() != null) {
			boolean validCurrency = mr.getEstimatedCost().getCurrency().equals("EUR") || mr.getEstimatedCost().getCurrency().equals("USD") || mr.getEstimatedCost().getCurrency().equals("GBP");
			super.state(validCurrency, "estimatedCost", "acme.validation.validCurrency.message");
		}

		boolean isDraft = mr.isDraftMode();

		if (!isDraft)
			super.state(isDraft, "*", "acme.validation.maintenanceRecord.published.message");

		boolean statusIsCompleted = mr.getStatus() == MaintenanceStatus.COMPLETED;

		if (!statusIsCompleted)
			super.state(statusIsCompleted, "status", "acme.validation.maintenanceRecord.not-completed.message");

		boolean valid = !tasksOfMr.isEmpty() && tasksOfMr.stream().allMatch(task -> !task.isDraftMode());

		if (!valid)
			super.state(valid, "*", "acme.validation.maintenanceRecord.incorrect-maintenanceTasks.message");

	}

	@Override
	public void perform(final MaintenanceRecord maintenanceRecord) {
		maintenanceRecord.setDraftMode(false);
		this.repository.save(maintenanceRecord);
	}

	@Override
	public void unbind(final MaintenanceRecord mr) {

	}

}
