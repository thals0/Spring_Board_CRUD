package com.sparta.board.dto;

import com.sparta.board.entity.Comment;
import com.sparta.board.entity.User;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentResponsDto {
    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private String username;

    public CommentResponsDto(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.createdAt = comment.getCreatedAt();
        this.modifiedAt = comment.getModifiedAt();
        this.username = comment.getUser().getUsername();
    }
}
