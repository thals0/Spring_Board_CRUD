package com.sparta.board.repository;

import com.sparta.board.entity.Board;
import com.sparta.board.entity.Comment;
import com.sparta.board.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Optional<Comment> findByIdAndPostAndUser(Long id, Board post, User user);
    Optional<Comment> findByIdAndUser(Long id, User user);
    void deleteByIdAndPostAndUser(Long id, Board post, User user);

    List<Comment> findAllByPostOrderByModifiedAtDesc(Board post);
}
