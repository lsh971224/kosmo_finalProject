package com.blue.bluearchive.admin.repository;

import com.blue.bluearchive.board.entity.Comment;
import com.blue.bluearchive.board.entity.CommentsComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface AdminCommentsCommentRepository extends JpaRepository<CommentsComment,Integer>, QuerydslPredicateExecutor<CommentsComment>
        , AdminCommentsCommentRepositoryCustom {
}
