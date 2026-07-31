package com.swisscom.task_manager.controller;

import com.swisscom.task_manager.enums.TaskPriority;
import com.swisscom.task_manager.enums.TaskStatus;
import com.swisscom.task_manager.model.TaskRequestDTO;
import com.swisscom.task_manager.model.TaskResponseDTO;
import com.swisscom.task_manager.security.UserPrincipal;
import com.swisscom.task_manager.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<Page<TaskResponseDTO>> getAllTasks(
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) TaskPriority priority,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        Page<TaskResponseDTO> tasks = taskService.getTasksForUser(currentUser.id(), status, priority, pageable);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> getTaskById(
            @PathVariable String id,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        return ResponseEntity.ok(taskService.getTaskByIdForUser(id, currentUser.id()));
    }

    @PostMapping
    public ResponseEntity<TaskResponseDTO> createTask(
            @Valid @RequestBody TaskRequestDTO requestDto,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        TaskResponseDTO createdTask = taskService.createTask(currentUser.id(), requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> updateTask(
            @PathVariable String id,
            @Valid @RequestBody TaskRequestDTO requestDto,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        return ResponseEntity.ok(taskService.updateTaskForUser(id, currentUser.id(), requestDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable String id,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        taskService.deleteTaskForUser(id, currentUser.id());
        return ResponseEntity.noContent().build();
    }
}