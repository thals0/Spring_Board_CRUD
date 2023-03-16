package com.sparta.board.controller;

import com.sparta.board.dto.BoardRequestDto;
import com.sparta.board.dto.BoardResponseDto;
import com.sparta.board.security.UserDetailsImpl;
import com.sparta.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class BoardController {
    private final BoardService boardService;

    // 모든 유저의 전체 글 반환
    @GetMapping("/allPosts")
    public List<BoardResponseDto> getAllPosts(){
        return boardService.getAllPosts();
    }

    // 해당 유저의 모든 글 반환
    @GetMapping("/posts")
    public List<BoardResponseDto> getPosts(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return boardService.getPosts(userDetails.getUser());
    }

    // username이 있는 사람만 작성
    // post 작성(저장)
    @PostMapping("/post")
    public BoardResponseDto createPost(@RequestBody BoardRequestDto boardRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return boardService.createPost(boardRequestDto, userDetails.getUser());
    }

    // 특정 글 반환
    // 각각의 게시글에 등록된 모든 댓글을 게시글과 같이 Client에 반환하기
    @GetMapping("/post/{postId}")
    public BoardResponseDto getPost(@PathVariable Long postId){
        return boardService.getPost(postId);
    }

    // 특정 글 수정
    @PutMapping("/post/{postId}")
    public BoardResponseDto updatePost(@PathVariable Long postId, @RequestBody BoardRequestDto boardRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return boardService.updatePost(postId, boardRequestDto, userDetails.getUser());
    }
    // 특정 글 삭제
    @DeleteMapping("/post/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable Long postId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        boardService.deletePost(postId, userDetails.getUser());
        return ResponseEntity.status(HttpStatus.OK).body("게시글 삭제 성공");
    }
}

