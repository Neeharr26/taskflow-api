package com.neehar.taskflow.comment.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.neehar.taskflow.comment.Comment;
import com.neehar.taskflow.common.Priority;
import com.neehar.taskflow.common.Status;
import com.neehar.taskflow.project.Project;
import com.neehar.taskflow.task.Task;
import com.neehar.taskflow.user.User;

public class CommentResponseTest {

	@Test
	void fromEntity_copiesFields_andFlattensOwner_andTask() {
		User assignee=User.builder().id(5L).name("Neehar").build();
		Project project =Project.builder().id(10L).name("TaskFlow").build();
		Task task=Task.builder().id(1L)
                .title("Write tests")
                .status(Status.TODO)
                .priority(Priority.HIGH)
                .project(project)
                .assignee(assignee)
                .build();

		// the thing under test: the mapper reads task.getId(),
				// author.getId() and author.getName() — so attach both
				Comment comment = Comment.builder()
						.id(100L)
						.content("Looks good to me")
						.task(task)
						.author(assignee)   // reusing the User above as the comment's author
						.build();

				// when
				CommentResponse response = CommentResponse.fromEntity(comment);

				// then
				assertThat(response.id()).isEqualTo(100L);
				assertThat(response.content()).isEqualTo("Looks good to me");
				assertThat(response.taskId()).isEqualTo(1L);            // flattened from task
				assertThat(response.authorId()).isEqualTo(5L);          // flattened from author
				assertThat(response.authorName()).isEqualTo("Neehar");  // flattened from author
	}
}
