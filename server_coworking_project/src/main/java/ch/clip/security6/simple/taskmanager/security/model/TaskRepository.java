package ch.clip.security6.simple.taskmanager.security.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    // Create task with associated user
    default Task save(Task task, TasksUser user) {
        task.setUser(user);
        return save(task);
    }

//    @Query("select t from Task t where t.user = :user")
    List<Task> findAllTasksByUser(TasksUser user);
    List<Task> findTasksByUserId(Long  userId);

}
