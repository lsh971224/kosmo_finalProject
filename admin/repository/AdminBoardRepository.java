package com.blue.bluearchive.admin.repository;

import com.blue.bluearchive.board.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminBoardRepository extends JpaRepository<Board,Integer>, QuerydslPredicateExecutor<Board>
        , AdminBoardRepositoryCustom {
}
