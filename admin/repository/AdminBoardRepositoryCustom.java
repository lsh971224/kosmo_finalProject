package com.blue.bluearchive.admin.repository;

import com.blue.bluearchive.admin.dto.AdminSearchDto;
import com.blue.bluearchive.board.entity.Board;
import com.blue.bluearchive.board.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminBoardRepositoryCustom {
    Page<Board> getBoardPage(AdminSearchDto commentSearchDto, Pageable pageable);

    Page<Board> getCategoryBoardPage(int category, AdminSearchDto boardSearchDto, Pageable pageable);


}
