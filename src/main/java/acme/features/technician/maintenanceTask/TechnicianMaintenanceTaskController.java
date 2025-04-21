
package acme.features.technician.maintenanceTask;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.maintenance.MaintenanceTask;
import acme.realms.technician.Technician;

@GuiController
public class TechnicianMaintenanceTaskController extends AbstractGuiController<Technician, MaintenanceTask> {

	@Autowired
	private TechnicianMaintenanceTaskListService	listService;

	@Autowired
	private TechnicianMaintenanceTaskShowService	showService;

	@Autowired
	private TechnicianMaintenanceTaskCreateService	createService;

	@Autowired
	private TechnicianMaintenanceTaskDeleteService	deleteService;


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("show", this.showService);
		super.addBasicCommand("create", this.createService);
		super.addBasicCommand("delete", this.deleteService);
	}
}
