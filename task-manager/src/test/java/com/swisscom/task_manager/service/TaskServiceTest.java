package com.swisscom.task_manager.service;

import com.swisscom.task_manager.entity.TaskEntity;
import com.swisscom.task_manager.enums.TaskPriority;
import com.swisscom.task_manager.enums.TaskStatus;
import com.swisscom.task_manager.exception.ResourceNotFoundException;
import com.swisscom.task_manager.mapper.TaskMapper;
import com.swisscom.task_manager.model.TaskRequestDTO;
import com.swisscom.task_manager.model.TaskResponseDTO;
import com.swisscom.task_manager.repository.TaskRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    private static final LocalDateTime FIXED_TIME = LocalDateTime.of(2026, 7, 30, 15, 0);
    private static final Long TEST_USER_ID = 1L;
    private static final Long OTHER_USER_ID = 2L;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskService taskService;

    @Test
    @DisplayName("Should create task successfully when request is valid")
    void shouldCreateTaskSuccessfully() {
        // Prepare
        TaskRequestDTO requestDto = createTestRequestDto();
        TaskEntity entityToSave = new TaskEntity(null, requestDto.title(), requestDto.description(), requestDto.status(), null, requestDto.priority(), null, null);
        TaskEntity savedEntity = new TaskEntity("12345", requestDto.title(), requestDto.description(), requestDto.status(), FIXED_TIME, requestDto.priority(), FIXED_TIME, TEST_USER_ID);
        TaskResponseDTO expectedResponse = createTestResponseDto("12345");

        Mockito.when(taskMapper.toEntity(requestDto)).thenReturn(entityToSave);
        Mockito.when(taskRepository.save(Mockito.any(TaskEntity.class))).thenReturn(savedEntity);
        Mockito.when(taskMapper.toDto(savedEntity)).thenReturn(expectedResponse);

        // Perform
        TaskResponseDTO result = taskService.createTask(TEST_USER_ID, requestDto);

        // Verify
        assertThat(result).isNotNull();
        assertThat(result).usingRecursiveComparison().isEqualTo(expectedResponse);

        ArgumentCaptor<TaskEntity> captor = ArgumentCaptor.forClass(TaskEntity.class);
        Mockito.verify(taskRepository).save(captor.capture());
        assertThat(captor.getValue().getUserId()).isEqualTo(TEST_USER_ID);
    }

    @Test
    @DisplayName("Should return task by ID when task exists and belongs to user")
    void shouldReturnTaskByIdWhenTaskExistsAndBelongsToUser() {
        // Prepare
        String taskId = "12345";
        TaskEntity existingEntity = createTestTaskEntity(taskId, TEST_USER_ID);
        TaskResponseDTO expectedResponse = createTestResponseDto(taskId);

        Mockito.when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingEntity));
        Mockito.when(taskMapper.toDto(existingEntity)).thenReturn(expectedResponse);

        // Perform
        TaskResponseDTO result = taskService.getTaskByIdForUser(taskId, TEST_USER_ID);

        // Verify
        assertThat(result).isNotNull();
        assertThat(result).usingRecursiveComparison().isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when task belongs to another user")
    void shouldThrowExceptionWhenTaskBelongsToAnotherUser() {
        // Prepare
        String taskId = "12345";
        TaskEntity existingEntity = createTestTaskEntity(taskId, OTHER_USER_ID);

        Mockito.when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingEntity));

        // Perform & Verify
        assertThatThrownBy(() -> taskService.getTaskByIdForUser(taskId, TEST_USER_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Task not found with id: " + taskId);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when task by ID does not exist")
    void shouldThrowExceptionWhenTaskNotFoundById() {
        // Prepare
        String taskId = "123124141";
        Mockito.when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        // Perform & Verify
        assertThatThrownBy(() -> taskService.getTaskByIdForUser(taskId, TEST_USER_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Task not found with id: " + taskId);
    }

    @Test
    @DisplayName("Should return page of tasks for specific user with filtering and pagination")
    void shouldReturnPageOfTasksForUser() {
        // Prepare
        Pageable pageable = PageRequest.of(0, 10);
        TaskEntity entity = createTestTaskEntity("12345", TEST_USER_ID);
        Page<TaskEntity> entityPage = new PageImpl<>(List.of(entity));
        TaskResponseDTO expectedResponse = createTestResponseDto("12345");

        Mockito.when(taskRepository.findAll(Mockito.any(Example.class), Mockito.eq(pageable)))
                .thenReturn(entityPage);
        Mockito.when(taskMapper.toDto(entity)).thenReturn(expectedResponse);

        // Perform
        Page<TaskResponseDTO> result = taskService.getTasksForUser(TEST_USER_ID, TaskStatus.TODO, TaskPriority.HIGH, pageable);

        // Verify
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst()).usingRecursiveComparison().isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("Should update task successfully when task belongs to user")
    void shouldUpdateTaskSuccessfully() {
        // Prepare
        String taskId = "12345";
        TaskRequestDTO updateRequest = new TaskRequestDTO("Updated Title", "Updated Desc", TaskStatus.IN_PROGRESS, TaskPriority.HIGH);
        TaskEntity existingEntity = createTestTaskEntity(taskId, TEST_USER_ID);
        TaskEntity updatedEntity = new TaskEntity(taskId, updateRequest.title(), updateRequest.description(), updateRequest.status(), FIXED_TIME, updateRequest.priority(), FIXED_TIME, TEST_USER_ID);
        TaskResponseDTO updatedResponse = new TaskResponseDTO(taskId, updateRequest.title(), updateRequest.description(), updateRequest.status(), FIXED_TIME, FIXED_TIME, updateRequest.priority());

        Mockito.when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingEntity));
        Mockito.when(taskRepository.save(existingEntity)).thenReturn(updatedEntity);
        Mockito.when(taskMapper.toDto(updatedEntity)).thenReturn(updatedResponse);

        // Perform
        TaskResponseDTO result = taskService.updateTaskForUser(taskId, TEST_USER_ID, updateRequest);

        // Verify
        assertThat(result).isNotNull();
        assertThat(result).usingRecursiveComparison().isEqualTo(updatedResponse);
    }

    @Test
    @DisplayName("Should delete task successfully when task belongs to user")
    void shouldDeleteTaskSuccessfully() {
        // Prepare
        String taskId = "12345";
        TaskEntity existingEntity = createTestTaskEntity(taskId, TEST_USER_ID);
        Mockito.when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingEntity));

        // Perform
        taskService.deleteTaskForUser(taskId, TEST_USER_ID);

        // Verify
        Mockito.verify(taskRepository).delete(existingEntity);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting non-existent task")
    void shouldThrowExceptionWhenDeletingNonExistentTask() {
        // Prepare
        String taskId = "1231234";
        Mockito.when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        // Perform & Verify
        assertThatThrownBy(() -> taskService.deleteTaskForUser(taskId, TEST_USER_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Task not found with id: " + taskId);

        Mockito.verify(taskRepository, Mockito.never()).delete(Mockito.any(TaskEntity.class));
    }

    // Helper Methods
    private TaskRequestDTO createTestRequestDto() {
        return new TaskRequestDTO("Test Task", "Description for test task", TaskStatus.TODO, TaskPriority.HIGH);
    }

    private TaskResponseDTO createTestResponseDto(String id) {
        return new TaskResponseDTO(id, "Test Task", "Description for test task", TaskStatus.TODO, FIXED_TIME, FIXED_TIME, TaskPriority.HIGH);
    }

    private TaskEntity createTestTaskEntity(String id, Long userId) {
        return new TaskEntity(id, "Test Task", "Description for test task", TaskStatus.TODO, FIXED_TIME, TaskPriority.HIGH, FIXED_TIME, userId);
    }
}