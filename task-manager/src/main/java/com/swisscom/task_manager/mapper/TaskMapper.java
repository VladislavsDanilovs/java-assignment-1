package com.swisscom.task_manager.mapper;

import com.swisscom.task_manager.entity.TaskEntity;
import com.swisscom.task_manager.model.TaskRequestDTO;
import com.swisscom.task_manager.model.TaskResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {
    public TaskEntity toEntity(TaskRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        TaskEntity entity = new TaskEntity();
        entity.setTitle(dto.title());
        entity.setDescription(dto.description());
        entity.setStatus(dto.status());
        entity.setPriority(dto.priority());
        return entity;
    }

    public TaskResponseDTO toDto(TaskEntity entity) {
        if (entity == null) {
            return null;
        }
        return new TaskResponseDTO(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getPriority()
        );
    }
}
