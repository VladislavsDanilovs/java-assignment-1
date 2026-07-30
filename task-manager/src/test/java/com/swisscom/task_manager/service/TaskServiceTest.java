package com.swisscom.task_manager.service;

import com.swisscom.task_manager.entity.TaskEntity;
import com.swisscom.task_manager.enums.TaskStatus;
import com.swisscom.task_manager.exception.ResourceNotFoundException;
import com.swisscom.task_manager.mapper.TaskMapper;
import com.swisscom.task_manager.model.TaskRequestDTO;
import com.swisscom.task_manager.model.TaskResponseDTO;
import com.swisscom.task_manager.repository.TaskRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskService taskService;

    private static final LocalDateTime FIXED_TIME = LocalDateTime.of(2026, 7, 30, 15, 0);

    private final TaskRequestDTO taskRequestDto = new TaskRequestDTO(
            "Test Task",
            "Description for test task",
            TaskStatus.TODO
    );

    private final TaskResponseDTO expectedResponseDto = new TaskResponseDTO(
            "12345",
            taskRequestDto.title(),
            taskRequestDto.description(),
            taskRequestDto.status(),
            FIXED_TIME
    );

    @Test
    @DisplayName("Should create task successfully")
    void createTask_Success() {
        // Prepare
        TaskEntity entityToSave = new TaskEntity(null, taskRequestDto.title(), taskRequestDto.description(), taskRequestDto.status(), null);
        TaskEntity savedEntity = new TaskEntity("12345", taskRequestDto.title(), taskRequestDto.description(), taskRequestDto.status(), FIXED_TIME);

        Mockito.when(taskMapper.toEntity(taskRequestDto)).thenReturn(entityToSave);
        Mockito.when(taskRepository.save(Mockito.any(TaskEntity.class))).thenReturn(savedEntity);
        Mockito.when(taskMapper.toDto(savedEntity)).thenReturn(expectedResponseDto);

        // Perform
        TaskResponseDTO result = taskService.createTask(taskRequestDto);

        // Verify
        assertThat(result).isNotNull();
        assertThat(result).usingRecursiveComparison().isEqualTo(expectedResponseDto);
        Mockito.verify(taskRepository).save(entityToSave);
    }

    @Test
    @DisplayName("Should return task by ID when task exists")
    void getTaskById_Success() {
        // Prepare
        String taskId = "12345";
        TaskEntity entity = new TaskEntity(taskId, "Test Task", "Description", TaskStatus.TODO, FIXED_TIME);

        Mockito.when(taskRepository.findById(taskId)).thenReturn(Optional.of(entity));
        Mockito.when(taskMapper.toDto(entity)).thenReturn(expectedResponseDto);

        // Perform
        TaskResponseDTO result = taskService.getTaskById(taskId);

        // Verify
        assertThat(result).isNotNull();
        assertThat(result).usingRecursiveComparison().isEqualTo(expectedResponseDto);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when task by ID does not exist")
    void getTaskById_NotFound() {
        // Prepare
        String taskId = "38124812498";
        Mockito.when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        // Perform & Verify
        assertThatThrownBy(() -> taskService.getTaskById(taskId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Task not found with id: " + taskId);
    }

    @Test
    @DisplayName("Should return list of all tasks")
    void getAllTasks_Success() {
        // Prepare
        TaskEntity entity = new TaskEntity("12345", "Test Task", "Description", TaskStatus.TODO, FIXED_TIME);
        Mockito.when(taskRepository.findAll()).thenReturn(List.of(entity));
        Mockito.when(taskMapper.toDto(entity)).thenReturn(expectedResponseDto);

        // Perform
        List<TaskResponseDTO> result = taskService.getAllTasks();

        // Verify
        assertThat(result).isNotNull().hasSize(1);
        assertThat(result.getFirst()).usingRecursiveComparison().isEqualTo(expectedResponseDto);
    }

    @Test
    @DisplayName("Should update task successfully")
    void updateTask_Success() {
        // Prepare
        String taskId = "12345";
        TaskRequestDTO updateRequest = new TaskRequestDTO("Updated Title", "Updated Desc", TaskStatus.IN_PROGRESS);
        TaskEntity existingEntity = new TaskEntity(taskId, "Old Title", "Old Desc", TaskStatus.TODO, FIXED_TIME);
        TaskEntity updatedEntity = new TaskEntity(taskId, updateRequest.title(), updateRequest.description(), updateRequest.status(), FIXED_TIME);

        TaskResponseDTO updatedResponse = new TaskResponseDTO(
                taskId, updateRequest.title(), updateRequest.description(), updateRequest.status(), FIXED_TIME
        );

        Mockito.when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingEntity));
        Mockito.when(taskRepository.save(existingEntity)).thenReturn(updatedEntity);
        Mockito.when(taskMapper.toDto(updatedEntity)).thenReturn(updatedResponse);

        // Perform
        TaskResponseDTO result = taskService.updateTask(taskId, updateRequest);

        // Verify
        assertThat(result).isNotNull();
        assertThat(result).usingRecursiveComparison().isEqualTo(updatedResponse);
    }

    @Test
    @DisplayName("Should delete task successfully when task exists")
    void deleteTask_Success() {
        // Prepare
        String taskId = "12345";
        Mockito.when(taskRepository.existsById(taskId)).thenReturn(true);

        // Perform
        taskService.deleteTask(taskId);

        // Verify
        Mockito.verify(taskRepository).deleteById(taskId);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting non-existent task")
    void deleteTask_NotFound() {
        // Prepare
        String taskId = "4241283412893";
        Mockito.when(taskRepository.existsById(taskId)).thenReturn(false);

        // Perform & Verify
        assertThatThrownBy(() -> taskService.deleteTask(taskId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Task not found with id: " + taskId);

        Mockito.verify(taskRepository, Mockito.never()).deleteById(taskId);
    }

    private TaskRequestDTO createTestRequestDto() {
        return new TaskRequestDTO("Test Task", "Description for test task", TaskStatus.TODO);
    }

    private TaskResponseDTO createTestResponseDto(String id) {
        return new TaskResponseDTO(id, "Test Task", "Description for test task", TaskStatus.TODO, FIXED_TIME);
    }
}