package com.sparta.board.controller;


import com.sparta.board.dto.CommentRequestDto;
import com.sparta.board.dto.CommentResponsDto;
import com.sparta.board.dto.StatusCodeDto;
import com.sparta.board.entity.Comment;
import com.sparta.board.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/post/{postId}/comment")
public class CommentController {
    private final CommentService commentService;

    // 해당 글에 모든 댓글을 가져올거야 (유저 상관 없이 !) -> boardController에서 하는ㄱㅓ 아님?
    // 일단 보류

    // 댓글 작성
    @PostMapping("/")
    public CommentResponsDto createComment(@PathVariable Long postId, @RequestBody CommentRequestDto commentRequestDto, HttpServletRequest req){
        return commentService.createComment(postId, commentRequestDto, req);
    }

    // 댓글 수정
    @PutMapping("/{commentId}")
    public CommentResponsDto updateComment(@PathVariable Long postId, @PathVariable Long commentId, @RequestBody CommentRequestDto commentRequestDto, HttpServletRequest req){
        return commentService.updateComment(postId, commentId, commentRequestDto, req);
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public StatusCodeDto deleteComment(@PathVariable Long postId, @PathVariable Long commentId, HttpServletRequest req){
        return commentService.deleteComment(postId, commentId, req);
    }
}
