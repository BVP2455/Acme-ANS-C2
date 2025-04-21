
package acme.features.technician.maintenanceTask;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.maintenance.MaintenanceTask;
import acme.realms.technician.Technician;

@GuiService
public class TechnicianMaintenanceTaskDeleteService extends AbstractGuiService<Technician, MaintenanceTask> {

	@Autowired
	private TechnicianMaintenanceTaskRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int id;
		MaintenanceTask mt;

		id = super.getRequest().getData("id", int.class);
		mt = this.repository.findMaintenanceTaskById(id);
		status = mt != null && super.getRequest().getPrincipal().hasRealm(mt.getMaintenanceRecord().getTechnician());

		super.getResponse().setAuthorised(status);
	}
	@Override
	public void load() {
		MaintenanceTask mt;
		int id;

		id = super.getRequest().getData("id", int.class);
		mt = this.repository.findMaintenanceTaskById(id);

		super.getBuffer().addData(mt);
	}

	@Override
	public void bind(final MaintenanceTask mt) {
		//		int taskId;
		//		Task task;
		//		MaintenanceRecord maintenanceRecord;
		//		int masterId;
		//		masterId = super.getRequest().getData("masterId", int.class);
		//		maintenanceRecord = this.repository.findMaintenanceRecordById(masterId);
		//		taskId = super.getRequest().getData("task", int.class);
		//		task = this.repository.findTaskById(taskId);

		super.bindObject(mt);
		//		mt.setTask(task);
		//		mt.setMaintenanceRecord(maintenanceRecord);
	}

	@Override
	public void validate(final MaintenanceTask mt) {
		;
	}

	@Override
	public void perform(final MaintenanceTask mt) {
		this.repository.delete(mt);
	}

	@Override
	public void unbind(final MaintenanceTask mt) {
		Dataset dataset;
		dataset = super.unbindObject(mt);
		// dataset.put("task", mt.getTask().getDescription());
		super.getResponse().addData(dataset);
	}

}
