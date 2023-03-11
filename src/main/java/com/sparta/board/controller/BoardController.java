package com.sparta.board.controller;

import com.sparta.board.dto.BoardRequestDto;
import com.sparta.board.dto.BoardResponsDto;
import com.sparta.board.dto.StatusCodeDto;
import com.sparta.board.entity.Board;
import com.sparta.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class BoardController {
    private final BoardService boardService;

    // 모든 유저의 전체 글 반환
    @GetMapping("/allPosts")
    public List<BoardResponsDto> getAllPosts(){
        return boardService.getAllPosts();
    }

    // 해당 유저의 모든 글 반환
    @GetMapping("/posts")
    public List<BoardResponsDto> getPosts(HttpServletRequest request){
        return boardService.getPosts(request);
    }

    // username이 있는 사람만 작성
    // post 작성(저장)
    @PostMapping("/post")
    public BoardResponsDto createPost(@RequestBody BoardRequestDto boardRequestDto, HttpServletRequest request){
        return boardService.createPost(boardRequestDto, request);
    }

    // 특정 글 반환
    // 각각의 게시글에 등록된 모든 댓글을 게시글과 같이 Client에 반환하기
    @GetMapping("/post/{postId}")
    public BoardResponsDto getPost(@PathVariable Long postId){
        return boardService.getPost(postId);
    }

    // 특정 글 수정
    @PutMapping("/post/{postId}")
    public BoardResponsDto updatePost(@PathVariable Long postId, @RequestBody BoardRequestDto boardRequestDto, HttpServletRequest request){
        return boardService.updatePost(postId, boardRequestDto, request);
    }
    // 특정 글 삭제
    @DeleteMapping("/post/{postId}")
    public StatusCodeDto deletePost(@PathVariable Long postId, HttpServletRequest request){
        return boardService.deletePost(postId, request);
    }
}

