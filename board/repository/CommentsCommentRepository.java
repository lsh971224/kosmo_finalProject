package com.blue.bluearchive.board.repository;

import com.blue.bluearchive.board.entity.Board;
import com.blue.bluearchive.board.entity.Comment;
import com.blue.bluearchive.board.entity.CommentsComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentsCommentRepository extends JpaRepository<CommentsComment,Integer> {
    List<CommentsComment> findByComment(Comment comment);

   // int countByCommentId(Comment commentId);


    //승훈 코드 추가
    List<CommentsComment> findByComment_Board(Board boardId);

    List<CommentsComment> findByCreatedByContaining(String keyword);
    List<CommentsComment> findByCommentsCommentContentContaining(String keyword);
}
