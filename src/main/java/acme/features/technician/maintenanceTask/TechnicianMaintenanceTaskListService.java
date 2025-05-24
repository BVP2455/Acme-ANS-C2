
package acme.features.technician.maintenanceTask;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.maintenance.MaintenanceRecord;
import acme.entities.maintenance.MaintenanceTask;
import acme.realms.technician.Technician;

@GuiService
public class TechnicianMaintenanceTaskListService extends AbstractGuiService<Technician, MaintenanceTask> {

	@Autowired
	private TechnicianMaintenanceTaskRepository repository;


	@Override
	public void authorise() {
		boolean status = false;
		int mrId;
		MaintenanceRecord mr;
		int technicianId;

		mrId = super.getRequest().getData("mrId", int.class);
		mr = this.repository.findMaintenanceRecordById(mrId);
		if (mr.isDraftMode()) {
			technicianId = super.getRequest().getPrincipal().getActiveRealm().getId();
			status = technicianId == mr.getTechnician().getId();
		} else
			status = super.getRequest().getPrincipal().hasRealmOfType(Technician.class) && mr != null;
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int mrId;
		Collection<MaintenanceTask> maintenanceTasks;

		mrId = super.getRequest().getData("mrId", int.class);
		maintenanceTasks = this.repository.findMaintenanceTasksByMaintenanceRecord(mrId);
		super.getBuffer().addData(maintenanceTasks);
	}

	@Override
	public void unbind(final MaintenanceTask mt) {
		int mrId;
		mrId = super.getRequest().getData("mrId", int.class);

		Dataset dataset;

		dataset = super.unbindObject(mt);
		dataset.put("type", mt.getTask().getType());
		dataset.put("description", mt.getTask().getDescription());
		dataset.put("priority", mt.getTask().getPriority());
		dataset.put("taskDraftMode", mt.getTask().isDraftMode());
		super.getResponse().addGlobal("mrId", mrId);
		super.getResponse().addData(dataset);
	}

	@Override
	public void unbind(final Collection<MaintenanceTask> mts) {
		int mrId;
		boolean draftMode;
		MaintenanceRecord mr;
		mrId = super.getRequest().getData("mrId", int.class);
		mr = this.repository.findMaintenanceRecordById(mrId);
		draftMode = mr.isDraftMode();

		super.getResponse().addGlobal("mrId", mrId);
		super.getResponse().addGlobal("draftMode", draftMode);
	}
}
