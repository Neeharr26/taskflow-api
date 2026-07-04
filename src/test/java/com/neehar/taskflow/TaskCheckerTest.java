package com.neehar.taskflow;

import org.junit.jupiter.api.Test;

import com.neehar.taskflow.common.Priority;
import com.neehar.taskflow.common.Status;
import com.neehar.taskflow.project.Project;
import com.neehar.taskflow.task.Task;
import com.neehar.taskflow.task.dto.TaskResponse;
import com.neehar.taskflow.user.User;

import static org.assertj.core.api.Assertions.assertThat;
public class TaskCheckerTest {

	@Test
	void fromEntity_mapsFields_whenTaskHasAssignee() {
		User assignee=User.builder().id(5L).name("Neehar").build();
		Project project =Project.builder().id(10L).name("TaskFlow").build();
		Task task=Task.builder().id(1L)
                .title("Write tests")
                .status(Status.TODO)
                .priority(Priority.HIGH)
                .project(project)
                .assignee(assignee)
                .build();
		// when
		  TaskResponse response = TaskResponse.fromEntity(task);
		  // then
	        assertThat(response.id()).isEqualTo(1L);
	        assertThat(response.title()).isEqualTo("Write tests");
	        assertThat(response.projectId()).isEqualTo(10L);
	        assertThat(response.assigneeId()).isEqualTo(5L);
	        assertThat(response.assigneeName()).isEqualTo("Neehar");
	}
	@Test
	void fromEntity_leavesAssigneeNull_WhenTaskHasNoAssignee() {
		Project project=Project.builder().id(10L).name("TaskFlow").build();
		Task task=Task.builder().id(2L)
                .title("Unassigned task")
                .status(Status.TODO)
                .priority(Priority.LOW)
                .project(project)
                .assignee(null)
                .build();
		
		 // when
        TaskResponse response = TaskResponse.fromEntity(task);

        // then
        assertThat(response.assigneeId()).isNull();
        assertThat(response.assigneeName()).isNull();
	}
}
