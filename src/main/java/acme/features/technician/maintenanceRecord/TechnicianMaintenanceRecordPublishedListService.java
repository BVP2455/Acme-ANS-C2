
package acme.features.technician.maintenanceRecord;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.maintenance.MaintenanceRecord;
import acme.realms.technician.Technician;

@GuiService
public class TechnicianMaintenanceRecordPublishedListService extends AbstractGuiService<Technician, MaintenanceRecord> {

	@Autowired
	private TechnicianMaintenanceRecordRepository repository;


	@Override
	public void authorise() {
		boolean status = super.getRequest().getPrincipal().hasRealmOfType(Technician.class);
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Collection<MaintenanceRecord> mrs;
		mrs = this.repository.findAllPublishedMaintenanceRecords();

		super.getBuffer().addData(mrs);
	}

	@Override
	public void unbind(final MaintenanceRecord mr) {
		Dataset dataset;

		dataset = super.unbindObject(mr, "draftMode", "maintenanceMoment", "status", "nextInspectionDue", "estimatedCost", "notes");
		dataset.put("aircraft", mr.getAircraft().getRegistrationNumber());
		super.addPayload(dataset, mr);
		super.getResponse().addData(dataset);
	}
}
