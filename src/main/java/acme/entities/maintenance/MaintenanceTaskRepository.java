
package acme.entities.maintenance;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface MaintenanceTaskRepository extends AbstractRepository {

	@Query("SELECT COUNT(mt) FROM MaintenanceTask mt WHERE mt.task.id = :taskId AND mt.maintenanceRecord.id = :maintenanceRecordId")
	long countByTaskIdAndMaintenanceRecordId(int taskId, int maintenanceRecordId);

}
