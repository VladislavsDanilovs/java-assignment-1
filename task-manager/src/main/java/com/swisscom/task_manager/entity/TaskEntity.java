package com.swisscom.task_manager.entity;

import com.swisscom.task_manager.enums.TaskPriority;
import com.swisscom.task_manager.enums.TaskStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Objects;

@Document(collection = "tasks")
public class TaskEntity {

    @Id
    private String id;
    private String title;
    private String description;
    private TaskStatus status;
    private LocalDateTime createdAt;
    private TaskPriority priority;
    private LocalDateTime updatedAt;

    public TaskEntity() {
    }

    public TaskEntity(String id, String title, String description, TaskStatus status, LocalDateTime createdAt,
                      TaskPriority priority, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.priority = priority;
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskEntity that = (TaskEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(title, that.title) && Objects.equals(description, that.description) && status == that.status && Objects.equals(createdAt, that.createdAt) && priority == that.priority && Objects.equals(updatedAt, that.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, status, createdAt, priority, updatedAt);
    }

    @Override
    public String toString() {
        return "TaskEntity{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", priority=" + priority +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
