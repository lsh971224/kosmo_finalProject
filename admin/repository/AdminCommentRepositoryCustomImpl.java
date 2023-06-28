package com.blue.bluearchive.admin.repository;

import com.blue.bluearchive.admin.dto.AdminSearchDto;
import com.blue.bluearchive.admin.entity.Category;
import com.blue.bluearchive.board.entity.Board;
import com.blue.bluearchive.board.entity.Comment;
import com.blue.bluearchive.board.entity.QComment;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityManager;
import java.util.List;

public class AdminCommentRepositoryCustomImpl implements AdminCommentRepositoryCustom {
    private JPAQueryFactory queryFactory;
    public AdminCommentRepositoryCustomImpl(EntityManager em){
        this.queryFactory=new JPAQueryFactory(em);
    }

    private BooleanExpression searchByLike(String searchBy, String searchQuery){
        if(StringUtils.equals("commentContent",searchBy)){
            return QComment.comment.commentContent.like("%"+searchQuery+"%");
        }else if(StringUtils.equals("createdBy",searchBy)) {
            return QComment.comment.createdBy.like("%"+searchQuery+"%");
        }
        return null;
    }
    @Override
    public Page<Comment> getCommentPage(AdminSearchDto commentSearchDto, Pageable pageable) {
        List<Comment> content=queryFactory
                .selectFrom(QComment.comment)
                .where(searchByLike(commentSearchDto.getSearchBy(),commentSearchDto.getSearchQuery())
                ).orderBy(QComment.comment.commentId.desc())
                .offset(pageable.getOffset()).limit(pageable.getPageSize())
                .fetch();
        long total = queryFactory.select(Wildcard.count).from(QComment.comment)
                .where(searchByLike(commentSearchDto.getSearchBy(),commentSearchDto.getSearchQuery())
                ).fetchOne();

        return new PageImpl<>(content,pageable,total);
    }

    @Override
    public Page<Comment> getBoardCommentPage(int categoryId, AdminSearchDto commentSearchDto, Pageable pageable) {
        List<Comment> content=queryFactory
                .selectFrom(QComment.comment)
                .where(QComment.comment.board.category.categoryId.eq(categoryId)
                        .and(searchByLike(commentSearchDto.getSearchBy(),commentSearchDto.getSearchQuery()))
                ).orderBy(QComment.comment.board.boardId.desc())
                .offset(pageable.getOffset()).limit(pageable.getPageSize())
                .fetch();
        long total = queryFactory.select(Wildcard.count).from(QComment.comment)
                .where(QComment.comment.board.category.categoryId.eq(categoryId)
                        .and(searchByLike(commentSearchDto.getSearchBy(), commentSearchDto.getSearchQuery()))
                ).fetchOne();

        return new PageImpl<>(content,pageable,total);
    }

}
