package com.blue.bluearchive.board.repository;

import com.blue.bluearchive.board.entity.Board;
import com.blue.bluearchive.board.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByBoard(Board boardId);

    int countByBoard(Board boardId);

    List<Comment> findByCreatedByContaining(String keyword);

    List<Comment> findByCommentContentContaining(String keyword);
}
