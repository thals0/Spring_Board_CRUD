package com.sparta.board.service;

import com.sparta.board.dto.CommentRequestDto;
import com.sparta.board.dto.CommentResponsDto;
import com.sparta.board.dto.StatusCodeDto;
import com.sparta.board.entity.Board;
import com.sparta.board.entity.Comment;
import com.sparta.board.entity.User;
import com.sparta.board.entity.UserRoleEnum;
import com.sparta.board.repository.BoardRepository;
import com.sparta.board.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final UserService userService;
    private final BoardService boardService;
    // 댓글 작성
    // 해당 게시글 , 유저 확인
    @Transactional
    public CommentResponsDto createComment(Long postId, CommentRequestDto commentRequestDto, HttpServletRequest req) {
        User user = userService.isLogin(req);
        // post도 받아와야 하는ㄷㅔ ? 아니면 postid 라던가 ..
        // 일단 client에서 api로 postid 받아옴
        Board post = boardService.isExistBoard(postId);
        // XXX: postId가 있으니까 굳이 post객체를 만들지 않고 그냥 postId값만 저장하면 안되나 ?
        //  boardRepository로 해당 postId를 가진 게시글이 존재하는지 확인해야함
        //  그럼 게시글이 존재하는지 확인하고 게시글이 있으면 postId를 날려주면 되는 거 아닌가 ?
        //  굳이 post를 날려줘야 하나 ?
        //  근데 생각해보니까 유저도 마찬가지 .. 이긴한데 유저도 토큰 검사해야하니까 필요한거같기도하고
        //  또 postId로 보내주려고 보니까 Comment entity에서 조인칼럼하고 private Board post로 하는데 .. 그럼 저것도 바꿔줘야 하나 ?
        //  일단 지금은 보류
        Comment comment = new Comment(commentRequestDto, user, post);
        commentRepository.save(comment);
        return new CommentResponsDto(comment);
    }


    // XXX : 수정, 삭제 할 때 postId가 필요할까? contentId만 있어도 수정하고 삭제할 땐 필요없을 것 같은데 ..
    //  삭제된 게시글에 있었던 댓글이 보여지면 안되니까 저렇게 처리하는게 좋나 ?
    //  근데 cascade하면 다 지워진다고 하셨던 것 같은데 (부모없는 자식없다)
    //  그리고 client 단에서 이미 처리가 되어서 오지 않을까
    //  굳이굳이 인것 같지만 일단 넣어놨음 .. 나중에 질문드려야지

    @Transactional
    // 댓글 수정
    public CommentResponsDto updateComment(Long postId, Long commentId, CommentRequestDto commentRequestDto, HttpServletRequest req) {
        // 토큰 검사 후 유효한 토큰이면서 해당 사용자가 작성한 댓글만 수정 가능
        User user = userService.isLogin(req);
        Board post = boardService.isExistBoard(postId);
        UserRoleEnum userRoleEnum = user.getRole();
        if(userRoleEnum == UserRoleEnum.ADMIN){
            Comment comment = commentRepository.findById(commentId).orElseThrow(
                    () -> new IllegalArgumentException("존재하지 않는 댓글입니다.")
            );
            comment.update(commentRequestDto.getContent());
            commentRepository.save(comment);
            return new CommentResponsDto(comment);
        } else{
            Comment comment = commentRepository.findByIdAndPostAndUser(commentId, post, user).orElseThrow(
                    ()-> new IllegalArgumentException("작성자만 삭제/수정할 수 있습니다.")
            );
            comment.update(commentRequestDto.getContent());
            commentRepository.save(comment);
            return new CommentResponsDto(comment);
        }
    }

    // TODO : @Transactional 공부하기 ~ 안쓰니까 에러났슴 하하하
    @Transactional
    // 댓글 삭제
    public StatusCodeDto deleteComment(Long postId, Long commentId, HttpServletRequest req) {
        User user = userService.isLogin(req);
        Board post = boardService.isExistBoard(postId);

        // 선택한 댓글의 DB 저장 유무 확인
//        Comment comment = commentRepository.findById(commentId).orElseThrow(
//                () -> new IllegalArgumentException("존재하지 않는 댓글입니다.")
//        );

        // 사용자 권한 가져와서 ADMIN이면 전체 수정 가능
        UserRoleEnum userRoleEnum = user.getRole();
        if(userRoleEnum == UserRoleEnum.ADMIN){
            commentRepository.deleteById(commentId);
            return ResponseEntity.ok(new StatusCodeDto(HttpStatus.OK.value(), "댓글 삭제 성공")).getBody();
        } else if (user != null){
            commentRepository.deleteByIdAndPostAndUser(commentId, post, user);
            return ResponseEntity.ok(new StatusCodeDto(HttpStatus.OK.value(), "댓글 삭제 성공")).getBody();
        } else{
            return ResponseEntity.ok(new StatusCodeDto(HttpStatus.OK.value(), "로그인이 필요한 서비스입니다. ")).getBody();
        }
    }
}
