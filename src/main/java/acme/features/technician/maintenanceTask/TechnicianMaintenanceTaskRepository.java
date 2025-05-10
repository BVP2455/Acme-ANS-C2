
package acme.features.technician.maintenanceTask;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.maintenance.MaintenanceRecord;
import acme.entities.maintenance.MaintenanceTask;
import acme.entities.maintenance.Task;

@Repository
public interface TechnicianMaintenanceTaskRepository extends AbstractRepository {

	@Query("select t from Task t where t.id = :id")
	Task findTaskById(int id);

	@Query("select mr from MaintenanceRecord mr where mr.id = :id")
	MaintenanceRecord findMaintenanceRecordById(int id);

	@Query("select mt from MaintenanceTask mt where mt.id = :id")
	MaintenanceTask findMaintenanceTaskById(int id);

	@Query("select t from Task t")
	Collection<Task> findAllTasks();

	@Query("select t from Task t where t not in (select mt.task from MaintenanceTask mt where mt.maintenanceRecord.id = :mrId) and (t.draftMode = false or t.technician.id = :technicianId)")
	Collection<Task> findAllAvailableTasks(int mrId, int technicianId);

	@Query("select mt from MaintenanceTask mt where mt.maintenanceRecord.id = :mrId")
	Collection<MaintenanceTask> findMaintenanceTasksByMaintenanceRecord(int mrId);

	@Query("select mt.task from MaintenanceTask mt where mt.maintenanceRecord.id = :mrId")
	Collection<Task> findTasksByMaintenanceRecord(int mrId);

}
