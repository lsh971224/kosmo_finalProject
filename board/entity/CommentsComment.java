package com.blue.bluearchive.board.entity;

import com.blue.bluearchive.report.entity.Report;
import com.blue.bluearchive.shop.entity.BaseEntity;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "comments_comment")
public class CommentsComment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comments_comment_id")
    private int commentsCommentId;

    @Column(name = "comments_comment_content", nullable = false)
    private String commentsCommentContent;

    @Column(name = "comments_comment_like_count")
    private Integer commentsCommentLikeCount;

    @Column(name = "comments_comment_hate_count")
    private Integer commentsCommentHateCount;

    @Column(name = "comments_comment_reports_count")
    private Integer commentsCommentReportsCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @OneToMany(mappedBy = "commentsComment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentsCommentLikeHate> commentsCommentLikeHate;

    @OneToMany(mappedBy = "commentsComment", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<Report> report;

    //승훈 추가
    //    //승훈 추가
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

}