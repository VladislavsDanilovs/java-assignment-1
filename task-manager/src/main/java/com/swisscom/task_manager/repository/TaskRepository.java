package com.swisscom.task_manager.repository;

import com.swisscom.task_manager.entity.TaskEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends MongoRepository<TaskEntity, String> {
    List<TaskEntity> findByUserId(Long userId);
}
