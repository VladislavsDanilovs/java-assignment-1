package com.swisscom.task_manager.service;

import com.swisscom.task_manager.entity.TaskEntity;
import com.swisscom.task_manager.entity.UserEntity;
import com.swisscom.task_manager.enums.TaskPriority;
import com.swisscom.task_manager.enums.TaskStatus;
import com.swisscom.task_manager.exception.ResourceNotFoundException;
import com.swisscom.task_manager.mapper.TaskMapper;
import com.swisscom.task_manager.model.TaskRequestDTO;
import com.swisscom.task_manager.model.TaskResponseDTO;
import com.swisscom.task_manager.repository.TaskRepository;
import com.swisscom.task_manager.repository.UserRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository, TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.taskMapper = taskMapper;
    }

    private UserEntity getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    public TaskResponseDTO createTask(String userEmail, TaskRequestDTO requestDto) {
        UserEntity user = getUserByEmail(userEmail);

        TaskEntity entity = taskMapper.toEntity(requestDto);
        entity.setUserId(user.getId());
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        TaskEntity savedEntity = taskRepository.save(entity);
        return taskMapper.toDto(savedEntity);
    }

    public Page<TaskResponseDTO> getTasksForUser(String userEmail, TaskStatus status, TaskPriority priority, Pageable pageable) {
        UserEntity user = getUserByEmail(userEmail);

        TaskEntity filterTemplate = new TaskEntity();
        filterTemplate.setUserId(user.getId());
        filterTemplate.setStatus(status);
        filterTemplate.setPriority(priority);

        Example<TaskEntity> example = Example.of(filterTemplate);

        return taskRepository.findAll(example, pageable)
                .map(taskMapper::toDto);
    }

    public TaskResponseDTO getTaskByIdForUser(String id, String userEmail) {
        UserEntity user = getUserByEmail(userEmail);

        TaskEntity entity = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        if (!entity.getUserId().equals(user.getId())) {
            throw new ResourceNotFoundException("Task not found with id: " + id);
        }

        return taskMapper.toDto(entity);
    }

    public TaskResponseDTO updateTaskForUser(String id, String userEmail, TaskRequestDTO requestDto) {
        UserEntity user = getUserByEmail(userEmail);

        TaskEntity existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        if (!existingTask.getUserId().equals(user.getId())) {
            throw new ResourceNotFoundException("Task not found with id: " + id);
        }

        existingTask.setTitle(requestDto.title());
        existingTask.setDescription(requestDto.description());
        existingTask.setStatus(requestDto.status());
        existingTask.setPriority(requestDto.priority());
        existingTask.setUpdatedAt(LocalDateTime.now());

        TaskEntity updatedEntity = taskRepository.save(existingTask);
        return taskMapper.toDto(updatedEntity);
    }

    public void deleteTaskForUser(String id, String userEmail) {
        UserEntity user = getUserByEmail(userEmail);

        TaskEntity existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        if (!existingTask.getUserId().equals(user.getId())) {
            throw new ResourceNotFoundException("Task not found with id: " + id);
        }

        taskRepository.deleteById(id);
    }
}