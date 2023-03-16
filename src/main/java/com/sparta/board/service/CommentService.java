package com.sparta.board.service;

import com.sparta.board.dto.CommentRequestDto;
import com.sparta.board.dto.CommentResponseDto;
import com.sparta.board.entity.Board;
import com.sparta.board.entity.Comment;
import com.sparta.board.entity.User;
import com.sparta.board.entity.UserRoleEnum;
import com.sparta.board.exception.CustomException;
import com.sparta.board.exception.ErrorCode;
import com.sparta.board.repository.BoardRepository;
import com.sparta.board.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final BoardService boardService;
    // 댓글 작성
    // 해당 게시글 , 유저 확인
    @Transactional
    public CommentResponseDto createComment(Long postId, CommentRequestDto commentRequestDto, User user) {
        Board post = boardService.isExistBoard(postId);
        Comment comment = new Comment(commentRequestDto, user, post);
        commentRepository.save(comment);
        return new CommentResponseDto(comment);
    }

    @Transactional
    // 댓글 수정
    public CommentResponseDto updateComment(Long postId, Long commentId, CommentRequestDto commentRequestDto, User user) {
        // 토큰 검사 후 유효한 토큰이면서 해당 사용자가 작성한 댓글만 수정 가능
        Board post = boardService.isExistBoard(postId);
        checkCommentRole(commentId, user);
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                    () -> new IllegalArgumentException("존재하지 않는 댓글입니다.")
            );
        comment.update(commentRequestDto.getContent());
        commentRepository.save(comment);
        return new CommentResponseDto(comment);
    }

    @Transactional
    // 댓글 삭제
    public void deleteComment(Long postId, Long commentId, User user) {
        Board post = boardService.isExistBoard(postId);
        checkCommentRole(commentId, user);
        commentRepository.deleteById(commentId);

    }

    // 접근 권한 확인 (관리자이거나, 해당 유저인 경우)
    private void checkCommentRole(Long commentId, User user) {
        if (user.getRole() == UserRoleEnum.ADMIN) return;
        commentRepository.findByIdAndUser(commentId, user).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );
    }
}
