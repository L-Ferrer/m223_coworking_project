package ch.clip.security6.simple.taskmanager.security.model;


import ch.clip.security6.simple.taskmanager.security.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final TaskRepository taskRepository;


    @Autowired
    public UserController(UserRepository userRepository, TaskRepository taskRepository,
                          BCryptPasswordEncoder bCryptPasswordEncoder) {
        super();
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;

    }

    // User endpoints
    @PostMapping("/sign-up")
    public void signUp(@RequestBody TasksUser user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }



    @PostMapping("/register")
    public ResponseEntity<?>  createUser (@RequestBody TasksUser user) {
        if (userRepository.findById(user.getId()).isPresent()) {
            return ResponseEntity
                    .badRequest()
                    .body("User already exists");
        } else {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(userRepository.save(user));
        }
    }

    @GetMapping
    public ResponseEntity<List<TasksUser>> getAllUsers() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(userRepository.findAll());


    }

    // Aufgabe 3 - User laden
    @GetMapping("/{id}")
    public ResponseEntity<TasksUser> getUser(@PathVariable Long id) {
        if (userRepository.findById(id).isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.OK) // http 200 ok
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(userRepository.findById(id).get());
        } else {
            return ResponseEntity
                    .notFound()
                    .build(); // http 404
        }


    }


    // Tasks Endpoint
    @PostMapping("/{userId}/tasks")
    public ResponseEntity<Task> createUserTask(@PathVariable Long userId, @RequestBody Task task) {

         if  (userRepository.findById(userId).isPresent()) {
             TasksUser user = userRepository.findById(userId).get();
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(taskRepository.save(task, user));

         } else {
               return ResponseEntity.notFound().build();
        }


    }

    @GetMapping("/{userId}/tasks")
    public ResponseEntity<Iterable<Task>> getUserTasks(@PathVariable Long userId) {
        if (userRepository.findById(userId).isPresent()) {
            TasksUser user = userRepository.findById(userId).get();
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(taskRepository.findAllTasksByUser(user));

        } else {
            return ResponseEntity.notFound().build();
        }

    }
}


