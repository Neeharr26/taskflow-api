package com.neehar.taskflow.comment.dto;

import java.time.LocalDateTime;

import com.neehar.taskflow.comment.Comment;

public record CommentResponse(
		 Long id,
	        String content,
	        Long taskId,
	        Long authorId,
	        String authorName,
	        LocalDateTime createdAt	) {
	 public static CommentResponse fromEntity(Comment comment) {
	        return new CommentResponse(
	                comment.getId(),
	                comment.getContent(),
	                comment.getTask().getId(),
	                comment.getAuthor().getId(),
	                comment.getAuthor().getName(),
	                comment.getCreatedAt()
	        );
	    }

}
