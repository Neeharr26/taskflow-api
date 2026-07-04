package com.neehar.taskflow.user.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.neehar.taskflow.user.User;

public class UserResponseTest {
	
	@Test
	void fromEntity_copiesUserFields() {
		User user=User.builder().id(1L).email("neehar@example.com").name("Neehar").build();
		
		UserResponse response=UserResponse.fromEntity(user);
		
		assertThat(response.id()).isEqualTo(1L);
		assertThat(response.email()).isEqualTo("neehar@example.com");
		assertThat(response.name()).isEqualTo("Neehar");
		
	}

}
