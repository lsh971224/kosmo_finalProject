package com.blue.bluearchive.admin.service;

import com.blue.bluearchive.admin.dto.AdminBoardDto;
import com.blue.bluearchive.admin.dto.AdminSearchDto;
import com.blue.bluearchive.admin.repository.AdminBoardRepository;
import com.blue.bluearchive.admin.repository.CategoryRepository;
import com.blue.bluearchive.board.entity.Board;
import com.blue.bluearchive.board.repository.BoardRepository;
import com.blue.bluearchive.report.repository.ReportBoardRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminBoardService {
    private final BoardRepository boardRepository;
    private final ModelMapper modelMapper;
    private final CategoryRepository categoryRepository;
    private final AdminBoardRepository adminBoardRepository;
    private final ReportBoardRepository reportBoardRepository;
//public Page<AdminBoardDto> getAllBoards(Pageable pageable) {
//    Page<Board> boardPage = boardRepository.findAll(pageable);
//    List<AdminBoardDto> boardDtos = new ArrayList<>();
//    for (Board board : boardPage.getContent()) {
//
//        AdminBoardDto boardDto = modelMapper.map(board, AdminBoardDto.class);
//        int size = reportBoardRepository.findByBoardBoardIdAndReportStatusFalse(board.getBoardId()).size();
//        boardDto.setNotReportCount(size);
//        boardDtos.add(boardDto);
//    }
//    return new PageImpl<>(boardDtos, boardPage.getPageable(), boardPage.getTotalElements());
//}
//
//    public Page<AdminBoardDto> getBoardsByCategoryId(int categoryId, Pageable pageable) {
//        Category category = categoryRepository.findById(categoryId)
//                .orElseThrow(() -> new NoSuchElementException("Category not found"));
//        Page<Board> boardPage = boardRepository.findByCategory(category, pageable);
//        List<AdminBoardDto> boardDtos = new ArrayList<>();
//        for (Board board : boardPage.getContent()) {
//            AdminBoardDto boardDto = modelMapper.map(board, AdminBoardDto.class);
//            int size = reportBoardRepository.findByBoardBoardIdAndReportStatusFalse(board.getBoardId()).size();
//            boardDto.setNotReportCount(size);
//            boardDtos.add(boardDto);
//        }
//        return new PageImpl<>(boardDtos, boardPage.getPageable(), boardPage.getTotalElements());
//    }
private Page<AdminBoardDto> getComment(Page<Board> boards) {
    List<AdminBoardDto> boardDtos = new ArrayList<>();
    for (Board board : boards.getContent()) {
        boardDtos.add(modelMapper.map(board, AdminBoardDto.class));
        int size = reportBoardRepository.findByBoardBoardIdAndReportStatusFalse(board.getBoardId()).size();
        boardDtos.get(boardDtos.size()-1).setNotReportCount(size);
    }
    long total = boards.getTotalElements();
    return new PageImpl<>(boardDtos, boards.getPageable(), total);
}

    @Transactional(readOnly = true)
    public Page<AdminBoardDto> getBoardByCreatedBy(int categoryid, AdminSearchDto searchDto, Pageable pageable){
        Page<AdminBoardDto> adminCommentDtoPage = null;
        Page<Board> boardCommentPage =null;
        if(categoryid==0){
            boardCommentPage = adminBoardRepository.getBoardPage(searchDto, pageable);
            adminCommentDtoPage = getComment(boardCommentPage);
        }else{
            boardCommentPage = adminBoardRepository.getCategoryBoardPage(categoryid, searchDto, pageable);
            adminCommentDtoPage = getComment(boardCommentPage);
        }
        return adminCommentDtoPage;
    }
}
