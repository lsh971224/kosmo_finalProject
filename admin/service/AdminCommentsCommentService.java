package com.blue.bluearchive.admin.service;


import com.blue.bluearchive.admin.dto.AdminCommentsCommentDto;
import com.blue.bluearchive.admin.dto.AdminSearchDto;
import com.blue.bluearchive.admin.entity.Category;
import com.blue.bluearchive.admin.repository.AdminCommentsCommentRepository;
import com.blue.bluearchive.board.entity.CommentsComment;
import com.blue.bluearchive.report.repository.ReportBoardRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminCommentsCommentService {
    private final AdminCommentsCommentRepository adminCommentsCommentRepository;
    private final ReportBoardRepository reportBoardRepository;

    private final ModelMapper modelMapper = new ModelMapper();

        private Page<AdminCommentsCommentDto> getComment(Page<CommentsComment> comments) {
        List<AdminCommentsCommentDto> commentsCommentDtos = new ArrayList<>();
        for (CommentsComment commentsComment : comments.getContent()) {
            commentsCommentDtos.add(modelMapper.map(commentsComment, AdminCommentsCommentDto.class));
            int size = reportBoardRepository.findByCommentsCommentCommentsCommentIdAndReportStatusFalse(commentsComment.getCommentsCommentId()).size();
            System.out.println("getComment" + size);
            commentsCommentDtos.get(commentsCommentDtos.size()-1).setNotReportCount(size);
        }
        long total = comments.getTotalElements();
        return new PageImpl<>(commentsCommentDtos, comments.getPageable(), total);
    }

    @Transactional(readOnly = true)
    public Page<AdminCommentsCommentDto> getCommentByCreatedBy(int categoryId, AdminSearchDto searchDto, Pageable pageable){
        Page<AdminCommentsCommentDto> adminCommentDtoPage;
        Page<CommentsComment> boardCommentPage;
        if(categoryId==0){
           boardCommentPage = adminCommentsCommentRepository.getCommentsCommentPage(searchDto, pageable);
           adminCommentDtoPage = getComment(boardCommentPage);
        }else {
            System.out.println("id=======================실행전" + categoryId);
            List<CommentsComment> commentsCommentList = adminCommentsCommentRepository.findAll();
            List<AdminCommentsCommentDto> commentsCommentDtos = new ArrayList<>();
            for (CommentsComment commentsComment : commentsCommentList) {
                if (commentsComment.getComment().getBoard().getCategory() != null
                        && commentsComment.getComment().getBoard().getCategory().getCategoryId() == categoryId
                        && searchByLike(searchDto, commentsComment)) {
                    int size = reportBoardRepository.findByCommentsCommentCommentsCommentIdAndReportStatusFalse(commentsComment.getCommentsCommentId()).size();
                    AdminCommentsCommentDto commentsCommentDto = modelMapper.map(commentsComment, AdminCommentsCommentDto.class);
                    commentsCommentDto.setNotReportCount(size);
                    commentsCommentDtos.add(commentsCommentDto);
                }
            }
            long total = Long.valueOf(commentsCommentDtos.size());
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), commentsCommentDtos.size());
            List<AdminCommentsCommentDto> commentDtos = commentsCommentDtos.subList(start, end);

            adminCommentDtoPage = new PageImpl<>(commentDtos, pageable, total);
        }
        return adminCommentDtoPage;
    }
    private boolean searchByLike(AdminSearchDto searchDto, CommentsComment commentsComment) {
        String searchBy = searchDto.getSearchBy();
        String searchQuery = searchDto.getSearchQuery();

        if (searchBy != null && searchQuery != null) {
            if (searchBy.equals("createdBy")) {
                return commentsComment.getComment().getCreatedBy().contains(searchQuery);
            } else if (searchBy.equals("commentsCommentContent")) {
                return commentsComment.getCommentsCommentContent().contains(searchQuery);
            }
        }

        return true; // 검색 조건이 없거나 지원하지 않는 검색 조건인 경우 모든 댓글을 가져옵니다.
    }

}
