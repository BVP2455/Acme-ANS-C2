
package acme.features.technician.task;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.maintenance.MaintenanceTask;
import acme.entities.maintenance.Task;

@Repository
public interface TechnicianTaskRepository extends AbstractRepository {

	@Query("select t from Task t where t.id = :id")
	Task findTaskById(int id);

	@Query("select mt from MaintenanceTask mt where mt.task.id = :id")
	Collection<MaintenanceTask> findMaintenanceTasksInTask(int id);

	@Query("select t from Task t where t.technician.id = :technicianId")
	Collection<Task> findAllTasksOfTechnician(int technicianId);

	@Query("select t from Task t where t.draftMode = false")
	Collection<Task> findAllPublishedTasks();

}
