
package acme.features.technician.maintenanceTask;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.maintenance.MaintenanceRecord;
import acme.entities.maintenance.MaintenanceTask;
import acme.realms.technician.Technician;

@GuiService
public class TechnicianMaintenanceTaskDeleteService extends AbstractGuiService<Technician, MaintenanceTask> {

	@Autowired
	private TechnicianMaintenanceTaskRepository repository;


	@Override
	public void authorise() {
		boolean status = false;
		int id;
		MaintenanceTask mt;
		int technicianId;

		if (!super.getRequest().getMethod().equals("GET")) {
			id = super.getRequest().getData("id", int.class);
			mt = this.repository.findMaintenanceTaskById(id);
			if (mt != null) {
				MaintenanceRecord mr = mt.getMaintenanceRecord();
				if (mr.isDraftMode()) {
					technicianId = super.getRequest().getPrincipal().getActiveRealm().getId();
					status = technicianId == mr.getTechnician().getId();
				}
			}
		}

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

		super.bindObject(mt);

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
