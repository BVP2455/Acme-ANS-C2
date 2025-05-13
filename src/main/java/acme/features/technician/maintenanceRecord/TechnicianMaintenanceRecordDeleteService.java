
package acme.features.technician.maintenanceRecord;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.maintenance.MaintenanceRecord;
import acme.entities.maintenance.MaintenanceTask;
import acme.realms.technician.Technician;

@GuiService
public class TechnicianMaintenanceRecordDeleteService extends AbstractGuiService<Technician, MaintenanceRecord> {

	@Autowired
	private TechnicianMaintenanceRecordRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int id;
		MaintenanceRecord mr;
		Technician technician;

		id = super.getRequest().getData("id", int.class);
		mr = this.repository.findMaintenanceRecordById(id);
		technician = mr == null ? null : mr.getTechnician();
		status = mr != null && mr.isDraftMode() && super.getRequest().getPrincipal().hasRealm(technician);

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
		super.bindObject(mr, "status", "nextInspectionDue", "estimatedCost", "notes");

	}

	@Override
	public void validate(final MaintenanceRecord mr) {

		boolean valid = mr.isDraftMode();

		if (!valid)
			super.state(valid, "*", "acme.validation.maintenanceRecord.published.message");
	}

	@Override
	public void perform(final MaintenanceRecord mr) {
		Collection<MaintenanceTask> maintenanceTasks;

		maintenanceTasks = this.repository.findMaintenanceTasksInMaintenanceRecord(mr.getId());
		this.repository.deleteAll(maintenanceTasks);
		this.repository.delete(mr);
	}
	@Override
	public void unbind(final MaintenanceRecord mr) {
		Dataset dataset;
		dataset = super.unbindObject(mr);
		super.getResponse().addData(dataset);
	}

}
