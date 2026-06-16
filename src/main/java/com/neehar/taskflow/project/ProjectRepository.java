package com.neehar.taskflow.project;

import org.springframework.data.jpa.repository.JpaRepository;

import com.neehar.taskflow.user.User;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
	List<Project> findByOwner(User owner);
}
