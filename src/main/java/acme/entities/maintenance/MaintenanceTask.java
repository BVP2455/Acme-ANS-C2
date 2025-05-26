
package acme.entities.maintenance;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.validation.Mandatory;
import acme.constraints.ValidMaintenanceTask;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@ValidMaintenanceTask
@Table(indexes = {
	@Index(columnList = "task_id"), @Index(columnList = "maintenance_record_id")
})

public class MaintenanceTask extends AbstractEntity {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Relationships ----------------------------------------------------------

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Task				task;

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private MaintenanceRecord	maintenanceRecord;

}
