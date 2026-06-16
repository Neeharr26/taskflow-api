package com.neehar.taskflow.task;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task,Long> {
	List<Task> findByProjectId(Long projectId);
}
