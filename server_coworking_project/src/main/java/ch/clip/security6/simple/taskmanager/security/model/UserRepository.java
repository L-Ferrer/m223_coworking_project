package ch.clip.security6.simple.taskmanager.security.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<TasksUser, Long> {
    Optional<TasksUser> findOneByUsername(String login);
    Optional<TasksUser> findById(long id);
    List<TasksUser> findAll();
}
