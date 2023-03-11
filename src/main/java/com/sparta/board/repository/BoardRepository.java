package com.sparta.board.repository;

import com.sparta.board.entity.Board;
import com.sparta.board.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {
    List<Board> findAllByOrderByModifiedAtDesc();
    List<Board> findAllByUserOrderByModifiedAtDesc(User user);
    Optional<Board> findByPostIdAndUser(Long postId, User user);
    void deleteByPostIdAndUser(Long postId, User user);
}
