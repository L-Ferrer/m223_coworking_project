package ch.clip.security6.simple.taskmanager.security.model;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.*;

/**
 *
 * @author Luigi Cavuoti
 *
 */
@Getter
@Setter
@ToString
@Entity
@Table(name = "user")
public class TasksUser implements Serializable {
//	private static final long serialVersionUID = 5558730372383087546L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(length = 50, unique = true, nullable = false)
	private String username;
	@Column(name = "hashed_password")
	private String password;
	private String salt;
	private String role;

//	@JsonManagedReference
/*	@OneToMany(mappedBy = "user", fetch=FetchType.LAZY)
	private List<Task> tasks= new ArrayList();*/

	public TasksUser() {
		super();
	}

	public TasksUser(String login, String hashedPassword, String salt) {
		super();
		this.username = login;
		this.password = hashedPassword;
		this.salt = salt;
		this.role = TasksUserRoles.MEMBER;
	}

	public TasksUser(String login, String hashedPassword, String salt, String role) {
		super();
		this.username = login;
		this.password = hashedPassword;
		this.salt = salt;
		this.role = TasksUserRoles.MEMBER;
	}

}
