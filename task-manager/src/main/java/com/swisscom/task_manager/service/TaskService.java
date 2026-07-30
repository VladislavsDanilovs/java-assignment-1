package com.swisscom.task_manager.service;

import com.swisscom.task_manager.entity.TaskEntity;
import com.swisscom.task_manager.enums.TaskPriority;
import com.swisscom.task_manager.enums.TaskStatus;
import com.swisscom.task_manager.exception.ResourceNotFoundException;
import com.swisscom.task_manager.mapper.TaskMapper;
import com.swisscom.task_manager.model.TaskRequestDTO;
import com.swisscom.task_manager.model.TaskResponseDTO;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.swisscom.task_manager.repository.TaskRepository;

import java.time.LocalDateTime;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    public TaskService(TaskRepository taskRepository, TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
    }

    public TaskResponseDTO createTask(TaskRequestDTO requestDto) {
        TaskEntity entity = taskMapper.toEntity(requestDto);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        TaskEntity savedEntity = taskRepository.save(entity);
        return taskMapper.toDto(savedEntity);
    }

    public Page<TaskResponseDTO> getAllTasks(TaskStatus status, TaskPriority priority, Pageable pageable) {
        TaskEntity filterTemplate = new TaskEntity();
        filterTemplate.setStatus(status);
        filterTemplate.setPriority(priority);

        Example<TaskEntity> example = Example.of(filterTemplate);

        return taskRepository.findAll(example, pageable)
                .map(taskMapper::toDto);
    }

    public TaskResponseDTO getTaskById(String id) {
        TaskEntity entity = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        return taskMapper.toDto(entity);
    }

    public TaskResponseDTO updateTask(String id, TaskRequestDTO requestDto) {
        TaskEntity existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        existingTask.setTitle(requestDto.title());
        existingTask.setDescription(requestDto.description());
        existingTask.setStatus(requestDto.status());
        existingTask.setUpdatedAt(LocalDateTime.now());

        TaskEntity updatedEntity = taskRepository.save(existingTask);
        return taskMapper.toDto(updatedEntity);
    }

    public void deleteTask(String id) {
        if (!taskRepository.existsById(id)) {
            throw new ResourceNotFoundException("Task not found with id: " + id);
        }
        taskRepository.deleteById(id);
    }
}
