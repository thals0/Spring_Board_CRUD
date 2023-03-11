package com.sparta.board.service;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.sparta.board.dto.BoardRequestDto;
import com.sparta.board.dto.BoardResponsDto;
import com.sparta.board.dto.CommentResponsDto;
import com.sparta.board.dto.StatusCodeDto;
import com.sparta.board.entity.Board;
import com.sparta.board.entity.Comment;
import com.sparta.board.entity.User;
import com.sparta.board.entity.UserRoleEnum;
import com.sparta.board.jwt.JwtUtil;
import com.sparta.board.repository.BoardRepository;
import com.sparta.board.repository.CommentRepository;
import com.sparta.board.repository.UserRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

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
    public List<BoardResponsDto> getAllPosts() {
        List<Board> posts = boardRepository.findAllByOrderByModifiedAtDesc();
        List<BoardResponsDto> boardResponsDtoList =new ArrayList<>();
        for(Board post: posts){
            ArrayList<CommentResponsDto> commentResponsDtos = new ArrayList<>();

            List<Comment> comments = commentRepository.findAllByPostOrderByModifiedAtDesc(post);
            for(Comment comment : comments){
                CommentResponsDto commentResponsDto = new CommentResponsDto(comment);
                commentResponsDtos.add(commentResponsDto);
            }
            BoardResponsDto boardResponsDto = new BoardResponsDto(post, commentResponsDtos);
            boardResponsDtoList.add(boardResponsDto);
        }

        return boardResponsDtoList;
    }

    // TODO : 관리자 권한 기능 추가

    @Transactional(readOnly = true)
    public List<BoardResponsDto> getPosts(HttpServletRequest request){
        User user = userService.isLogin(request);
        List<BoardResponsDto> boardResponsDtoList =new ArrayList<>();
        // 해당 유저에 모든 글 불러옴
        List<Board> posts = boardRepository.findAllByUserOrderByModifiedAtDesc(user);
        // posts들 돌면서 해당 posts의 postid에 해당하는 댓글들 모두 찾아오기
        for(Board post: posts){
            ArrayList<CommentResponsDto> commentResponsDtos = new ArrayList<>();

            // post의 postid로 해당 게시글 댓글 모두 찾아오기
            List<Comment> comments = commentRepository.findAllByPostOrderByModifiedAtDesc(post);
            for(Comment comment : comments){
                CommentResponsDto commentResponsDto = new CommentResponsDto(comment);
                commentResponsDtos.add(commentResponsDto);
            }
            BoardResponsDto boardResponsDto = new BoardResponsDto(post, commentResponsDtos);
            boardResponsDtoList.add(boardResponsDto);
        }
        // return으로 boardResponDtoList이렇게 주면 되겠다 .!!
        return boardResponsDtoList;
    }

    // 로그인이 되어있으면 글 작성 할 수 있도록
    // userid도 같이 저장
    @Transactional
    public BoardResponsDto createPost(BoardRequestDto boardRequestDto, HttpServletRequest request) {
        User user = userService.isLogin(request);
        Board post = new Board(boardRequestDto, user);
        ArrayList<CommentResponsDto> commentResponsDtos = new ArrayList<>();
        boardRepository.save(post);
        return new BoardResponsDto(post, commentResponsDtos);
    }

    @Transactional
    // BoardResponsDto로 넣어줘야 하는거 아닌가 ? -> 수정 완
    // 게시글에 등록된 모든 댓글을 게시글과 같이 Client에 반환하기
    public BoardResponsDto getPost(Long postId) {
        Board post = boardRepository.findById(postId).orElseThrow(
                ()-> new IllegalArgumentException("존재하지 않는 글 입니다.")
        );
        ArrayList<CommentResponsDto> commentResponsDtos = new ArrayList<>();

        List<Comment> comments = commentRepository.findAllByPostOrderByModifiedAtDesc(post);
        for(Comment comment : comments){
            CommentResponsDto commentResponsDto = new CommentResponsDto(comment);
            commentResponsDtos.add(commentResponsDto);
        }
        return new BoardResponsDto(post, commentResponsDtos);
    }

    @Transactional
    // 유저 확인 후 수정
    // XXX: 여기서는 사실 commentResponsDtos를 날려줄 필요가 없긴한데
    public BoardResponsDto updatePost(Long postId, BoardRequestDto boardRequestDto, HttpServletRequest request) {
        User user = userService.isLogin(request);
        ArrayList<CommentResponsDto> commentResponsDtos = new ArrayList<>();

        // 사용자 권한 가져와서 ADMIN이면 전체 수정 가능
        UserRoleEnum userRoleEnum = user.getRole();
        if(userRoleEnum == UserRoleEnum.ADMIN){
            Board post = boardRepository.findById(postId).orElseThrow(
                    () -> new IllegalArgumentException("존재하지 않는 게시글입니다.")
            );
            post.update(boardRequestDto.getTitle(), boardRequestDto.getContent());
            boardRepository.save(post);
            return new BoardResponsDto(post, commentResponsDtos);
        } else{
            Board post = boardRepository.findByPostIdAndUser(postId, user).orElseThrow(
                    ()-> new IllegalArgumentException("작성자만 삭제/수정할 수 있습니다.")
            );
            post.update(boardRequestDto.getTitle(), boardRequestDto.getContent());
            boardRepository.save(post);
            return new BoardResponsDto(post, commentResponsDtos);
        }

    }

    @Transactional
    // 유저 확인 후 삭제
    public StatusCodeDto deletePost(Long postId, HttpServletRequest request) {
        User user = userService.isLogin(request);
        // TODO: user의 post를 전부 가져온 후 거기서 postid로 비교 ?

        // 사용자 권한 가져와서 ADMIN이면 전체 수정 가능
        UserRoleEnum userRoleEnum = user.getRole();
        if(userRoleEnum == UserRoleEnum.ADMIN){
            boardRepository.deleteById(postId);
            return ResponseEntity.ok(new StatusCodeDto(HttpStatus.OK.value(), "게시글 삭제 성공")).getBody();
        } else if (user != null){
            boardRepository.deleteByPostIdAndUser(postId, user);
            return ResponseEntity.ok(new StatusCodeDto(HttpStatus.OK.value(), "게시글 삭제 성공")).getBody();
        } else{
            return ResponseEntity.ok(new StatusCodeDto(HttpStatus.OK.value(), "로그인이 필요한 서비스입니다. ")).getBody();
        }

    }

    // 게시글 존재 여부 확인
    public Board isExistBoard(Long postId){
        return boardRepository.findById(postId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 게시글입니다.")
        );
    }
}
