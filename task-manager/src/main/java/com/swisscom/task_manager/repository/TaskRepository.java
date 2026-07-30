package com.swisscom.task_manager.repository;

import com.swisscom.task_manager.entity.TaskEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends MongoRepository<TaskEntity, String> {
}
