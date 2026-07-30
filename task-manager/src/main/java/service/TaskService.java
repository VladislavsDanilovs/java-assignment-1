package service;

import entity.TaskEntity;
import org.springframework.stereotype.Service;
import repository.TaskRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public TaskEntity createTask(TaskEntity task) {
        if (task == null) {
            return null;
        }

        if (task.getCreatedAt() == null) {
            task.setCreatedAt(LocalDateTime.now());
        }
        return taskRepository.save(task);
    }

    public List<TaskEntity> getAllTasks() {
        return taskRepository.findAll();
    }

    public Optional<TaskEntity> getTaskById(String id) {
        return taskRepository.findById(id);
    }

    public Optional<TaskEntity> updateTask(String id, TaskEntity updatedTask) {
        return taskRepository.findById(id)
                .map(existingTask -> {
                    existingTask.setTitle(updatedTask.getTitle());
                    existingTask.setDescription(updatedTask.getDescription());
                    existingTask.setStatus(updatedTask.getStatus());
                    return taskRepository.save(existingTask);
                });
    }

    public boolean deleteTask(String id) {
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
