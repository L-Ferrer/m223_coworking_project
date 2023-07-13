package ch.clip.security6.simple.taskmanager.security.model;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    static Logger log = Logger.getAnonymousLogger();
    //@Autowired
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskController(TaskRepository taskRepository, UserRepository userRepository) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
    }

    // https://spring.io/guides/gs/rest-service-cors/
    @CrossOrigin(origins = "http://localhost:3000")
    // Aufgabe 2 – Showsliste laden
    @GetMapping
    public ResponseEntity<List<Task>> getTasks() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(taskRepository.findAll());
    }



    // Aufgabe 3 - Einzelner Show laden
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTask(@PathVariable Long id) {
        if (taskRepository.findById(id).isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.OK) // http 200 ok
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(taskRepository.findById(id).get());
        } else {
            return ResponseEntity
                    .notFound()
                    .build(); // http 404
        }


    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    Task newTask(@RequestBody Task newTask) {
        log.info(newTask.toString());
        return taskRepository.save(newTask);
    }

    // Aufgabe 5 - Task bearbeiten
    @PutMapping("/{id}")
//    @ResponseStatus(HttpStatus.RESET_CONTENT)
    public ResponseEntity<Task> editTask(@PathVariable long id, @RequestBody Task newTask) {
        Optional<Task> currentTask = taskRepository.findById(id);
        currentTask
//        showRepository.findById(id)
                .map(task -> {
                    task.setId(newTask.getId());
                    task.setDescription(newTask.getDescription());
                    return ResponseEntity
                            .status(HttpStatus.RESET_CONTENT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(taskRepository.save(task));
                })
                .orElseGet(() -> {
                    newTask.setId(id);
                    return ResponseEntity
                            .status(HttpStatus.CREATED)
                            .body(taskRepository.save(newTask));
                });
        return ResponseEntity.notFound().build(); // 404
    }

    // Aufgabe 6 - Task löschen
    @DeleteMapping("/{id}")
    public ResponseEntity deleteTask(@PathVariable long id) {
        if (taskRepository.findById(id).isPresent()) {
            taskRepository.deleteById(id);
            return ResponseEntity
                    .status(HttpStatus.RESET_CONTENT)
                    .build();
        } else {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)   // 404 not found
                    .build(); // empty body
        }
    }

    // Aufgabe 7 - ShowsListe löschen
    @DeleteMapping("")
    public ResponseEntity<List<Task>> deleteAllTasks() {
        if (taskRepository.findAll().isEmpty()) {
            return ResponseEntity.notFound().build(); // 404
        } else {
            taskRepository.deleteAll();
            return ResponseEntity
                    .status(HttpStatus.RESET_CONTENT)
                    .build();
        }
    }


    // get authenticated user
    @GetMapping("/my-tasks")
    public ResponseEntity<List<Task>> getTasksForCurrentUser() {
        // Get the authenticated user's username
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // find the current user
        TasksUser tasksUser = userRepository.findOneByUsername(username).get();
        log.info(tasksUser.toString());

        // Pass the username to the task service
        List<Task> tasks = taskRepository.findTasksByUserId(tasksUser.getId());

        return ResponseEntity.ok(tasks);
    }
}
