package ch.clip.security6.simple.taskmanager;

import ch.clip.security6.simple.taskmanager.security.model.TasksUser;
import ch.clip.security6.simple.taskmanager.security.model.TasksUserRoles;
import ch.clip.security6.simple.taskmanager.security.model.UserRepository;
import ch.clip.security6.simple.taskmanager.security.model.Task;
import ch.clip.security6.simple.taskmanager.security.model.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCrypt;

@SpringBootApplication
public class TaskmanagerApplication {
	private static final Logger log = LoggerFactory.getLogger(TaskmanagerApplication.class);
	public static void main(String[] args) {
		SpringApplication.run(TaskmanagerApplication.class, args);
	}

	@Bean
	public CommandLineRunner demoUsers(UserRepository userRepository, TaskRepository repository) {
		return (args) -> {
			// save a couple of entities

			log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>CommandlineRunner::demoTasksWithJwt");
			String salt = BCrypt.gensalt();

			log.info("salt : "+salt);
			TasksUser u1 = new TasksUser("admin", BCrypt.hashpw("123",salt), salt, TasksUserRoles.ADMIN);
			TasksUser u2 = new TasksUser("member", BCrypt.hashpw("123",salt), salt, TasksUserRoles.MEMBER);
			TasksUser u3 = new TasksUser("customer", BCrypt.hashpw("123",salt), salt, TasksUserRoles.CUSTOMER);
			TasksUser u4 = new TasksUser("lou", BCrypt.hashpw("123",salt), salt, TasksUserRoles.MEMBER);
			TasksUser u5 = new TasksUser("al", BCrypt.hashpw("123",salt), salt, TasksUserRoles.MEMBER);

			userRepository.save(u1);
			userRepository.save(u2);
			userRepository.save(u3);
			userRepository.save(u4);
			userRepository.save(u5);
			// fetch all tasks
			log.info("Users found with findAll():");
			log.info("-------------------------------");
			for (TasksUser user : userRepository.findAll()) {
				log.info(user.toString());
			}
			log.info("");
			log.info("User and Roles persisted in DemoDataLoader");

			// save a couple of customers
			Task t1 = new Task(u1,"Dinner with my Family");
			Task t2 = new Task(u2,"movie Night with my Family");
			Task t3 = new Task(u3,"Brunch with my Students");


			repository.save(t1);
			repository.save(t2);
			repository.save(t3);


			// fetch all tasks
			log.info("Customers found with findAll():");
			log.info("-------------------------------");
			for (Task task : repository.findAll()) {
				log.info(task.toString());
			}
			log.info("");

			// fetch an individual Task by ID
			repository.findById(1L).ifPresent(task -> {
				log.info("Task found with findById(1L):");
				log.info("--------------------------------");
				log.info(task.toString());
				log.info("");
			});

			log.info("tasks");





		};
	}


}
