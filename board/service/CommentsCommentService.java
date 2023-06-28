package com.blue.bluearchive.board.service;

import com.blue.bluearchive.admin.dto.AdminCommentsCommentDto;
import com.blue.bluearchive.board.dto.CommentDto;
import com.blue.bluearchive.board.dto.CommentFormDto;
import com.blue.bluearchive.board.dto.CommentsCommentDto;
import com.blue.bluearchive.board.dto.CommentsCommentFormDto;
import com.blue.bluearchive.board.entity.Board;
import com.blue.bluearchive.board.entity.Comment;
import com.blue.bluearchive.board.entity.CommentsComment;
import com.blue.bluearchive.board.entity.CommentsCommentLikeHate;
import com.blue.bluearchive.board.repository.*;
import com.blue.bluearchive.member.dto.CustomUserDetails;
import com.blue.bluearchive.report.repository.ReportBoardRepository;
import com.blue.bluearchive.shop_inquiry.entity.Inquiry;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CommentsCommentService {
    private final CommentRepository commentRepository;
    private final CommentsCommentRepository commentsCommentRepository;
    private final ModelMapper modelMapper;

    private final CommentsCommentLikeHateRepository commentsCommentLikeHateRepository;
    private final ReportBoardRepository reportBoardRepository;


    public List<CommentsCommentDto> getCommentsCommentByCommentId(int commentId){
        Comment comment = commentRepository.findById(commentId).orElseThrow();
        if (comment == null) {
            // 예외 처리 또는 오류 처리
            return Collections.emptyList(); // 빈 목록 반환하거나 예외 처리 로직 추가
        }
        List<CommentsComment> commentsComments = commentsCommentRepository.findByComment(comment);
        if (commentsComments == null) {
            // 예외 처리 또는 오류 처리
            return Collections.emptyList(); // 빈 목록 반환하거나 예외 처리 로직 추가
        }
        List<CommentsCommentDto> commentsCommentDtos = new ArrayList<>();
        for(CommentsComment commentsComment : commentsComments){
            commentsCommentDtos.add(modelMapper.map(commentsComment, CommentsCommentDto.class));
        }
        return commentsCommentDtos;
    }


    //건희추가
    @Transactional(readOnly = false)
    public Integer save(CommentsCommentFormDto commentsCommentFormDto) throws  Exception{
        CommentsComment commentsComment = commentsCommentFormDto.createBoard();
        commentsCommentRepository.save(commentsComment);
        return commentsComment.getCommentsCommentId();
    }

    //건희추가
    @Transactional(readOnly = false)
    public void update(int commentsCommentId, String commentsCommentContent){
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        Object principal = authentication.getPrincipal();
        CustomUserDetails userDetails = (CustomUserDetails) principal;
        String  loginMemberIdx=Long.toString(userDetails.getIdx());

        CommentsComment commentsComment = commentsCommentRepository.findById(commentsCommentId)
                .orElseThrow(()->new IllegalArgumentException("유효하지않은 ID입니다"));
        if(!commentsComment.getCreatedBy().equals(loginMemberIdx)) {
            throw new AccessDeniedException("현재 로그인되어있는 사용자와 작성자가 일치하지 않습니다");
        }
        commentsComment.setCommentsCommentContent(commentsCommentContent);
    }

    //건희추가
    @Transactional(readOnly = false)
    public void delete(int commentsCommentId) {
        CommentsComment commentsComment = commentsCommentRepository.findById(commentsCommentId)
                .orElseThrow(()->new IllegalArgumentException("유효하지않은 ID입니다"));
        commentsCommentRepository.delete(commentsComment);
    }
    public void incrementCommentsCommentLikeCount(int commentsCommentId) {
        CommentsComment commentsComment = commentsCommentRepository.findById(commentsCommentId)
                .orElseThrow(() -> new NoSuchElementException("CommentsComment not found"));
        if (commentsComment != null) {
            commentsComment.setCommentsCommentLikeCount(commentsComment.getCommentsCommentLikeCount() + 1);
            commentsCommentRepository.save(commentsComment);
        }
    }
    public void incrementCommentsCommentHateCount(int commentsCommentId) {
        CommentsComment commentsComment = commentsCommentRepository.findById(commentsCommentId)
                .orElseThrow(() -> new NoSuchElementException("CommentsComment not found"));
        if (commentsComment != null) {
            commentsComment.setCommentsCommentHateCount(commentsComment.getCommentsCommentHateCount() + 1);
            commentsCommentRepository.save(commentsComment);
        }
    }
    public void decreaseCommentsCommentLikeCount(int commentsCommentId) {
        CommentsComment commentsComment = commentsCommentRepository.findById(commentsCommentId)
                .orElseThrow(() -> new NoSuchElementException("CommentsComment not found"));
        if (commentsComment != null) {
            commentsComment.setCommentsCommentLikeCount(commentsComment.getCommentsCommentLikeCount() - 1);
            commentsCommentRepository.save(commentsComment);
        }
    }
    public void decreaseCommentsCommentHateCount(int commentsCommentId) {
        CommentsComment commentsComment = commentsCommentRepository.findById(commentsCommentId)
                .orElseThrow(() -> new NoSuchElementException("CommentsComment not found"));
        if (commentsComment != null) {
            commentsComment.setCommentsCommentHateCount(commentsComment.getCommentsCommentHateCount() - 1);
            commentsCommentRepository.save(commentsComment);
        }
    }
    public int getCommentsCommentLikeCount(int commentsCommentId) {
        CommentsComment commentsComment = commentsCommentRepository.findById(commentsCommentId).orElse(null);
        return (commentsComment != null) ? commentsComment.getCommentsCommentLikeCount() : 0;
    }
    public int getCommentsCommentsHateCount(int commentsCommentId) {
        CommentsComment commentsComment = commentsCommentRepository.findById(commentsCommentId).orElse(null);
        return (commentsComment != null) ? commentsComment.getCommentsCommentHateCount() : 0;
    }
    //승훈 코드 추가
    private final BoardRepository boardRepository;
    public List<AdminCommentsCommentDto> getCommentsCommentByCategoryId(int categoryId) {
        List<AdminCommentsCommentDto> commentsCommentDtos = new ArrayList<>();

        if(categoryId == 0){
            List<Board> boards = boardRepository.findAll();

            for (Board board : boards) {
                List<CommentsComment> commentsComments = commentsCommentRepository.findByComment_Board(board);
                for (CommentsComment commentsComment : commentsComments) {
                    AdminCommentsCommentDto commentsCommentDtoDto = modelMapper.map(commentsComment, AdminCommentsCommentDto.class);
                    commentsCommentDtos.add(commentsCommentDtoDto);
                    int size = reportBoardRepository.findByCommentsCommentCommentsCommentIdAndReportStatusFalse(commentsComment.getCommentsCommentId()).size();
                    commentsCommentDtos.get(commentsCommentDtos.size()-1).setNotReportCount(size);
                }
            }
        }else{
            List<Board> boards = boardRepository.findByCategoryCategoryId(categoryId);

            for (Board board : boards) {
                List<CommentsComment> comments = commentsCommentRepository.findByComment_Board(board);
                for (CommentsComment commentsComment : comments) {
                    AdminCommentsCommentDto commentsCommentDto = modelMapper.map(commentsComment, AdminCommentsCommentDto.class);
                    commentsCommentDtos.add(commentsCommentDto);
                    int size = reportBoardRepository.findByCommentsCommentCommentsCommentIdAndReportStatusFalse(commentsComment.getCommentsCommentId()).size();
                    commentsCommentDtos.get(commentsCommentDtos.size()-1).setNotReportCount(size);
                }
            }
        }

        return commentsCommentDtos;
    }

    public List<AdminCommentsCommentDto> getCommentsComment() {
        List<AdminCommentsCommentDto> commentsCommentDtos = new ArrayList<>();
        List<Board> boards = boardRepository.findAll();
        for (Board board : boards) {
            List<CommentsComment> commentsComments = commentsCommentRepository.findByComment_Board(board);
            for (CommentsComment commentsComment : commentsComments) {
                AdminCommentsCommentDto commentsCommentDto = modelMapper.map(commentsComment, AdminCommentsCommentDto.class);
                commentsCommentDtos.add(commentsCommentDto);
                int size = reportBoardRepository.findByCommentsCommentCommentsCommentIdAndReportStatusFalse(commentsComment.getCommentsCommentId()).size();
                commentsCommentDtos.get(commentsCommentDtos.size()-1).setNotReportCount(size);
            }
        }
        return commentsCommentDtos;
    }


    public void deletes(List<Integer> commentsCommentIds) {
        for (Integer commentsCommentId : commentsCommentIds) {
            // Retrieve the reply by its ID
            CommentsComment commentsComment = commentsCommentRepository.findById(commentsCommentId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid commentsCommentId: " + commentsCommentId));
            System.out.println(commentsComment.getCommentsCommentId()+"아이디아이디");
            commentsCommentRepository.deleteById(commentsComment.getCommentsCommentId());
        }
    }


    public List<AdminCommentsCommentDto> searchCommentsComment(String option, String keyword) {
        List<CommentsComment> commentsComments;
        List<AdminCommentsCommentDto> commentsCommentList = new ArrayList<>();

        switch (option) {
            case "1": // 작성자
                commentsComments = commentsCommentRepository.findByCreatedByContaining(keyword);
                break;
            case "2": // 내용
                commentsComments = commentsCommentRepository.findByCommentsCommentContentContaining(keyword);
                break;
            default:
                commentsComments = Collections.emptyList();
                break;
        }

        for (CommentsComment commentsComment : commentsComments) {
            commentsCommentList.add(modelMapper.map(commentsComment, AdminCommentsCommentDto.class));
            int size = reportBoardRepository.findByCommentsCommentCommentsCommentIdAndReportStatusFalse(commentsComment.getCommentsCommentId()).size();
            commentsCommentList.get(commentsCommentList.size()-1).setNotReportCount(size);
        }

        return commentsCommentList;
    }

}
