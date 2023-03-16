package com.sparta.board.dto;

import com.sparta.board.entity.Board;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
public class BoardResponseDto {
    private final Long postid;
    private final String user;
    private final String title;
    private final String content;
    private List<CommentResponseDto> commentResponseDtoList;

    public BoardResponseDto(Board post) {
        this.postid = post.getPostId();
        this.user = post.getUser().getUsername();
        this.title = post.getTitle();
        this.content = post.getContent();
    }

    public BoardResponseDto(Board board, List<CommentResponseDto> commentResponsDtoList) {
        this.postid = board.getPostId();
        this.user = board.getUser().getUsername();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.commentResponseDtoList = commentResponsDtoList;
    }
}
