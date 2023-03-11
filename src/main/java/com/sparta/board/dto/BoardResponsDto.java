package com.sparta.board.dto;

import com.sparta.board.entity.Board;
import lombok.Getter;

import java.util.List;

@Getter
public class BoardResponsDto {
    private final Long postid;
    private final String user;
    private final String title;
    private final String content;
    private final List<CommentResponsDto> commentResponsDtoList;

    public BoardResponsDto(Board board, List<CommentResponsDto> commentResponsDtoList) {
        this.postid = board.getPostId();
        this.user = board.getUser().getUsername();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.commentResponsDtoList = commentResponsDtoList;
    }
}
