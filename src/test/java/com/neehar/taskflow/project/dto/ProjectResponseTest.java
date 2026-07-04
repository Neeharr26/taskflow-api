package com.neehar.taskflow.project.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.neehar.taskflow.project.Project;
import com.neehar.taskflow.user.User;

public class ProjectResponseTest {

	@Test
	void fromEntity_copiesFields_andFlattensOwner() {
		User owner= User.builder().id(1L).name("Neehar").email("neehar@example.com").build();
		Project project= Project.builder().id(10L).name("TaskFlow").description("A task management API").owner(owner).build();
		
		 // when
        ProjectResponse response = ProjectResponse.fromEntity(project);
        
        // then
        assertThat(response.id()).isEqualTo(10L);
        assertThat(response.name()).isEqualTo("TaskFlow");
        assertThat(response.description()).isEqualTo("A task management API");
        assertThat(response.ownerId()).isEqualTo(1L);      // flattened from owner
        assertThat(response.ownerName()).isEqualTo("Neehar"); // flattened from owner
	}
}
