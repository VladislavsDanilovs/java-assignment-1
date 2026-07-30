package com.swisscom.task_manager.model;

import com.swisscom.task_manager.enums.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TaskRequestDTO(
        @NotBlank
        String title,
        String description,
        @NotNull
        TaskStatus status
) {
}
