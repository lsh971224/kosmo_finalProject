package com.blue.bluearchive.board.repository;

import com.blue.bluearchive.board.entity.CommentsComment;
import com.blue.bluearchive.board.entity.CommentsCommentLikeHate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentsCommentLikeHateRepository extends JpaRepository<CommentsCommentLikeHate,Integer> {

    CommentsCommentLikeHate findByCommentsCommentCommentsCommentIdAndMemberIdx(int commentsCommentId,Long idx);

    //승훈 코드 추가
    // 대댓글 삭제에 필요한 로직
    List<CommentsCommentLikeHate> findByCommentsComment(CommentsComment commentsComment);
}
