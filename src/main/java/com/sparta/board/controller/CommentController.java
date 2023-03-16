package com.sparta.board.controller;


import com.sparta.board.dto.CommentRequestDto;
import com.sparta.board.dto.CommentResponseDto;
import com.sparta.board.security.UserDetailsImpl;
import com.sparta.board.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/post/{postId}/comment")
public class CommentController {
    private final CommentService commentService;

    // 댓글 작성
    @PostMapping("/")
    public CommentResponseDto createComment(@PathVariable Long postId, @RequestBody CommentRequestDto commentRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return commentService.createComment(postId, commentRequestDto, userDetails.getUser());
    }

    // 댓글 수정
    @PutMapping("/{commentId}")
    public CommentResponseDto updateComment(@PathVariable Long postId, @PathVariable Long commentId, @RequestBody CommentRequestDto commentRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return commentService.updateComment(postId, commentId, commentRequestDto, userDetails.getUser());
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable Long postId, @PathVariable Long commentId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        commentService.deleteComment(postId, commentId, userDetails.getUser());
        return ResponseEntity.status(HttpStatus.OK).body("댓글 삭제 성공");
    }
}
