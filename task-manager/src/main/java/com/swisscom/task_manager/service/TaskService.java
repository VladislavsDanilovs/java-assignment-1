package com.swisscom.task_manager.service;

import com.swisscom.task_manager.entity.TaskEntity;
import com.swisscom.task_manager.enums.TaskPriority;
import com.swisscom.task_manager.enums.TaskStatus;
import com.swisscom.task_manager.exception.ResourceNotFoundException;
import com.swisscom.task_manager.mapper.TaskMapper;
import com.swisscom.task_manager.model.TaskRequestDTO;
import com.swisscom.task_manager.model.TaskResponseDTO;
import com.swisscom.task_manager.repository.TaskRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    public TaskService(TaskRepository taskRepository, TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
    }

    public TaskResponseDTO createTask(Long userId, TaskRequestDTO requestDto) {
        TaskEntity entity = taskMapper.toEntity(requestDto);
        entity.setUserId(userId);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        TaskEntity savedEntity = taskRepository.save(entity);
        return taskMapper.toDto(savedEntity);
    }

    public Page<TaskResponseDTO> getTasksForUser(Long userId, TaskStatus status, TaskPriority priority, Pageable pageable) {
        TaskEntity filterTemplate = new TaskEntity();
        filterTemplate.setUserId(userId);
        filterTemplate.setStatus(status);
        filterTemplate.setPriority(priority);

        Example<TaskEntity> example = Example.of(filterTemplate);

        return taskRepository.findAll(example, pageable)
                .map(taskMapper::toDto);
    }

    public TaskResponseDTO getTaskByIdForUser(String id, Long userId) {
        TaskEntity entity = getTaskEntityAndVerifyOwner(id, userId);
        return taskMapper.toDto(entity);
    }

    public TaskResponseDTO updateTaskForUser(String id, Long userId, TaskRequestDTO requestDto) {
        TaskEntity existingTask = getTaskEntityAndVerifyOwner(id, userId);

        existingTask.setTitle(requestDto.title());
        existingTask.setDescription(requestDto.description());
        existingTask.setStatus(requestDto.status());
        existingTask.setPriority(requestDto.priority());
        existingTask.setUpdatedAt(LocalDateTime.now());

        TaskEntity updatedEntity = taskRepository.save(existingTask);
        return taskMapper.toDto(updatedEntity);
    }

    public void deleteTaskForUser(String id, Long userId) {
        TaskEntity existingTask = getTaskEntityAndVerifyOwner(id, userId);
        taskRepository.delete(existingTask);
    }

    private TaskEntity getTaskEntityAndVerifyOwner(String id, Long userId) {
        TaskEntity task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        if (!task.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Task not found with id: " + id);
        }

        return task;
    }
}