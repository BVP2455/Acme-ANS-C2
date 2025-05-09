
package acme.features.technician.maintenanceRecord;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircraft.Aircraft;
import acme.entities.maintenance.MaintenanceRecord;
import acme.entities.maintenance.MaintenanceStatus;
import acme.entities.maintenance.TaskType;
import acme.realms.technician.Technician;

@GuiService
public class TechnicianMaintenanceRecordUpdateService extends AbstractGuiService<Technician, MaintenanceRecord> {

	@Autowired
	private TechnicianMaintenanceRecordRepository repository;


	@Override
	public void authorise() {
		boolean status = false;
		int mrId = super.getRequest().getData("id", int.class);
		MaintenanceRecord mr = this.repository.findMaintenanceRecordById(mrId);
		Technician technician = mr != null ? mr.getTechnician() : null;

		if (mr != null && mr.isDraftMode() && super.getRequest().getPrincipal().hasRealm(technician)) {
			status = true;

			if (super.getRequest().getMethod().equals("POST")) {
				Aircraft aircraft = super.getRequest().getData("aircraft", Aircraft.class);
				Aircraft existingAircraft = aircraft != null ? this.repository.findAircraftById(aircraft.getId()) : null;
				boolean aircraftValid = existingAircraft != null && aircraft.getId() != 0;

				String statusInput = super.getRequest().getData("status", String.class);
				boolean statusValid = false;
				if (statusInput != null)
					for (TaskType tt : TaskType.values())
						if (tt.name().equalsIgnoreCase(statusInput.trim())) {
							statusValid = true;
							break;
						}
				status = status && statusValid && aircraftValid;
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
		super.bindObject(mr, "nextInspectionDue", "estimatedCost", "notes", "status", "aircraft");
	}

	@Override
	public void validate(final MaintenanceRecord mr) {
		boolean confirmation;
		confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");
	}

	@Override
	public void perform(final MaintenanceRecord mr) {
		this.repository.save(mr);
	}

	@Override
	public void unbind(final MaintenanceRecord mr) {
		Dataset dataset;
		SelectChoices statusChoices;
		statusChoices = SelectChoices.from(MaintenanceStatus.class, mr.getStatus());
		SelectChoices aircraftChoices;
		Collection<Aircraft> aircrafts;
		aircrafts = this.repository.findAllAircrafts();
		aircraftChoices = SelectChoices.from(aircrafts, "registrationNumber", mr.getAircraft());
		dataset = super.unbindObject(mr, "nextInspectionDue", "estimatedCost", "notes");
		dataset.put("statuses", statusChoices);
		dataset.put("aircrafts", aircraftChoices);
		dataset.put("aircraft", aircraftChoices.getSelected().getKey());
		super.getResponse().addData(dataset);
	}

}
