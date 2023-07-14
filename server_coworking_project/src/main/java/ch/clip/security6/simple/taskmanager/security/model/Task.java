package ch.clip.security6.simple.taskmanager.security.model;

import java.util.Date;

import ch.clip.security6.simple.taskmanager.security.model.TasksUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Setter
@Getter
@ToString
@Entity
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String description;
    private Date date;

    @ManyToOne
    @JoinColumn(name="user_id")
    private TasksUser user;

    protected Task() { }
    public Task(TasksUser user, String description) {
        this.user = user;
        this.description = description;
        this.date = new Date();
    }
    public Task(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }
}
