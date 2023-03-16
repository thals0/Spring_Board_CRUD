package com.sparta.board.service;

import com.sparta.board.dto.BoardRequestDto;
import com.sparta.board.dto.BoardResponseDto;
import com.sparta.board.dto.CommentResponseDto;
import com.sparta.board.entity.Board;
import com.sparta.board.entity.Comment;
import com.sparta.board.entity.User;
import com.sparta.board.entity.UserRoleEnum;
import com.sparta.board.exception.CustomException;
import com.sparta.board.exception.ErrorCode;
import com.sparta.board.jwt.JwtUtil;
import com.sparta.board.repository.BoardRepository;
import com.sparta.board.repository.CommentRepository;
import com.sparta.board.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final JwtUtil jwtUtil;

    // 모든 유저의 모든 글 반환
    @Transactional(readOnly = true) // ✨AOP, jpa entity매니저 transaction안에서 동작
    public List<BoardResponseDto> getAllPosts() {
        List<Board> posts = boardRepository.findAllByOrderByModifiedAtDesc();
        List<BoardResponseDto> boardResponseDtoList =new ArrayList<>();
        for(Board post: posts){
            List<CommentResponseDto> commentResponseDtos = getCommentResponseList(post);
            boardResponseDtoList.add(new BoardResponseDto(post, commentResponseDtos));
        }

        return boardResponseDtoList;
    }

    // TODO : 관리자 권한 기능 추가

    @Transactional(readOnly = true)
    public List<BoardResponseDto> getPosts(User user){
//        User user = userService.isLogin(request);
        // 해당 유저에 모든 글 불러옴
        List<Board> posts = boardRepository.findAllByUserOrderByModifiedAtDesc(user);
        List<BoardResponseDto> boardResponseDtoList =new ArrayList<>();
        // posts들 돌면서 해당 posts의 postid에 해당하는 댓글들 모두 찾아오기
        for(Board post: posts){
            List<CommentResponseDto> commentResponseDtos = getCommentResponseList(post);
            boardResponseDtoList.add(new BoardResponseDto(post, commentResponseDtos));
        }
        return boardResponseDtoList;
    }

    // 로그인이 되어있으면 글 작성 할 수 있도록
    // userid도 같이 저장
    @Transactional
    public BoardResponseDto createPost(BoardRequestDto boardRequestDto, User user) {
//        User user = userService.isLogin(request);
        Board post = new Board(boardRequestDto, user);
        boardRepository.save(post);
        return new BoardResponseDto(post);
    }

    @Transactional
    // BoardResponseDto로 넣어줘야 하는거 아닌가 ? -> 수정 완
    // 게시글에 등록된 모든 댓글을 게시글과 같이 Client에 반환하기
    public BoardResponseDto getPost(Long postId) {
        Board post = isExistBoard(postId);
        return new BoardResponseDto(post, getCommentResponseList(post));
    }

    @Transactional
    // 유저 확인 후 수정
    public BoardResponseDto updatePost(Long postId, BoardRequestDto boardRequestDto, User user) {
//        User user = userService.isLogin(request);
        Board post = isExistBoard(postId);    // 게시글이 존재하는지 확인 후 가져온다
        checkPostRole(postId, user);  // 권한을 확인한다 (자신이 쓴 글인지 확인)
        post.update(boardRequestDto.getTitle(), boardRequestDto.getContent());
        return new BoardResponseDto(post, getCommentResponseList(post));
    }

    // 해당 게시글의 댓글 리스트 반환하는 매서드
    private List<CommentResponseDto> getCommentResponseList (Board post) {
        List<CommentResponseDto> commentResponseDtos = new ArrayList<>();
        for (Comment comment : post.getComments()) {
            commentResponseDtos.add(new CommentResponseDto(comment));
        }
        return commentResponseDtos;
    }

    @Transactional
    // 유저 확인 후 삭제
    public void deletePost(Long postId, User user) {
//        User user = userService.isLogin(request);
        checkPostRole(postId, user);
        boardRepository.deleteById(postId);
    }

    // 접근 권한 확인 (관리자이거나, 해당 유저인 경우)
    private void checkPostRole(Long postId, User user) {
        if (user.getRole() == UserRoleEnum.ADMIN) return;
        boardRepository.findByPostIdAndUser(postId, user).orElseThrow(
                // TODO : 커스텀 한 예외처리 예시
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );
    }

    // 게시글 존재 여부 확인
    public Board isExistBoard(Long postId){
        return boardRepository.findById(postId).orElseThrow(
                () -> new CustomException(ErrorCode.POST_NOT_FOUND)
//                () -> new IllegalArgumentException("존재하지 않는 게시글입니다.")
        );
    }
}
