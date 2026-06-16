package com.neehar.taskflow.comment;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.neehar.taskflow.comment.dto.CommentCreateRequest;
import com.neehar.taskflow.comment.dto.CommentResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tasks/{taskId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentResponse> create(
            @PathVariable Long taskId,
            @Valid @RequestBody CommentCreateRequest request,
            @AuthenticationPrincipal UserDetails currentUser
    ) {
        CommentResponse response = commentService.create(taskId, request, currentUser.getUsername());
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping
    public ResponseEntity<List<CommentResponse>> list(
            @PathVariable Long taskId,
            @AuthenticationPrincipal UserDetails currentUser
    ) {
        return ResponseEntity.ok(commentService.listByTask(taskId, currentUser.getUsername()));
    }
}
