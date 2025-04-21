
package acme.features.technician.maintenanceRecord;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.aircraft.Aircraft;
import acme.entities.maintenance.MaintenanceRecord;
import acme.entities.maintenance.MaintenanceTask;
import acme.entities.maintenance.Task;

@Repository
public interface TechnicianMaintenanceRecordRepository extends AbstractRepository {

	@Query("select mr from MaintenanceRecord mr where mr.technician.id = :technicianId")
	Collection<MaintenanceRecord> findAllMaintenanceRecordsOfTechnician(int technicianId);

	@Query("select mr from MaintenanceRecord mr where mr.id = :id")
	MaintenanceRecord findMaintenanceRecordById(int id);

	@Query("select a from Aircraft a where a.id = :id")
	Aircraft findAircraftById(int id);

	@Query("select a from Aircraft a")
	Collection<Aircraft> findAllAircrafts();

	@Query("select mt.task from MaintenanceTask mt where mt.maintenanceRecord.id = :id")
	Collection<Task> findTasksInMaintenanceRecord(int id);

	@Query("select mt from MaintenanceTask mt where mt.maintenanceRecord.id = :id")
	Collection<MaintenanceTask> findMaintenanceTasksInMaintenanceRecord(int id);

	@Query("select mr from MaintenanceRecord mr")
	Collection<MaintenanceRecord> findAllMaintenanceRecords();
}
