package com.swisscom.task_manager.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.swisscom.task_manager.enums.TaskPriority;
import com.swisscom.task_manager.enums.TaskStatus;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record TaskResponseDTO(
        String id,
        String title,
        String description,
        TaskStatus status,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime createdAt,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime updatedAt,
        @NotNull
        TaskPriority priority
) {
}
