package at.undok.undok.client.repository;

import at.undok.undok.client.model.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TaskRepo extends JpaRepository<Task, UUID>  {

    List<Task> findByCaseEntity_Id(UUID caseId);
    List<Task> findByStatusIn(List<String> statuses);

}
