package com.blue.bluearchive.admin.controller;

import com.blue.bluearchive.admin.dto.*;
import com.blue.bluearchive.admin.entity.Category;
import com.blue.bluearchive.admin.service.AdminBoardService;
import com.blue.bluearchive.admin.service.AdminCommentService;
import com.blue.bluearchive.admin.service.AdminCommentsCommentService;
import com.blue.bluearchive.admin.service.CategoryService;
import com.blue.bluearchive.board.service.BoardService;
import com.blue.bluearchive.board.service.CommentService;
import com.blue.bluearchive.board.service.CommentsCommentService;
import com.blue.bluearchive.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.util.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
@Slf4j
public class AdminController {
    private final CategoryService categoryService;
    private final BoardService boardService;
    private final CommentService commentService;
    private final CommentsCommentService commentsCommentService;
    private final ReportService reportService;
    private final AdminBoardService adminBoardService;
    private final AdminCommentService adminCommentService;
    private final AdminCommentsCommentService adminCommentsCommentService;

    @GetMapping("/main")
    public String adminMain() {
        return "adminPage/manageMain";
    }

    @GetMapping("/userMgt")
    public String userMgt() {
        return "adminPage/manageUser";
    }

    @GetMapping("/shopMgt")
    public String shopMgt() {
        return "adminPage/manageShop";
    }

    @GetMapping("/sellUserMgt")
    public String sellUserMgt() {
        return "/adminPage/manageUserSell";
    }

@GetMapping("/boardMgt/{page}")
public String getBoardMgt(@RequestParam int id, Model model, AdminSearchDto adminSearchDto,
                                    @PathVariable("page")Optional<Integer> page) {
    System.out.println("adminSearchDto===="+adminSearchDto);

    List<CategoryDto> categoryDtoList = categoryService.getAllCategory();
    model.addAttribute("categoryList", categoryDtoList);
    //페이지 설정
    Pageable pageable = PageRequest.of(page.get(),5);
    model.addAttribute("maxPage",5);
    //리스트 데이터들 페이지 객체화
    Page<AdminBoardDto> boardDtoList =  adminBoardService.getBoardByCreatedBy(id, adminSearchDto, pageable);
    model.addAttribute("boardDtoList", boardDtoList);
    model.addAttribute("searchDto", adminSearchDto);
    model.addAttribute("id", id);
    return "/adminPage/manageCommunity_board";  // Return as JSON
}
@GetMapping("/commentMgt/{page}")
public String getBoardCommentMgt(@RequestParam int id, Model model, AdminSearchDto adminSearchDto,
                                    @PathVariable("page")Optional<Integer> page) {

    List<CategoryDto> categoryDtoList = categoryService.getAllCategory();
    model.addAttribute("categoryList", categoryDtoList);
    //페이지 설정
    Pageable pageable = PageRequest.of(page.get(),5);
    model.addAttribute("maxPage",5);
    //리스트 데이터들 페이지 객체화
    Page<AdminCommentDto> commentDtos =  adminCommentService.getCommentByCreatedBy(id, adminSearchDto, pageable);
    model.addAttribute("commentDtos", commentDtos);
    model.addAttribute("searchDto", adminSearchDto);
    model.addAttribute("id", id);
    return "adminPage/manageCommunity_comment";  // Return as JSON
}
    @GetMapping("/commentsCommentMgt/{page}")
    public String getCommentsCommentMgt(@RequestParam int id,AdminSearchDto searchDto,Model model
                                        ,  @PathVariable("page")Optional<Integer> page) {
        List<CategoryDto> categoryDtoList = categoryService.getAllCategory();
        model.addAttribute("categoryList", categoryDtoList);
        System.out.println("id 아이디 아이디 아이디 "+ id);
        //페이지 설정
        Pageable pageable = PageRequest.of(page.get(),5);
        model.addAttribute("maxPage",5);
        //리스트 데이터들 페이지 객체화
        Page<AdminCommentsCommentDto> commentsCommentDtos =  adminCommentsCommentService.getCommentByCreatedBy(id, searchDto, pageable);
        model.addAttribute("commentsCommentDtos", commentsCommentDtos);
        model.addAttribute("searchDto", searchDto);
        model.addAttribute("id", id);
        return "adminPage/manageCommunity_commentsComment";  // Return as JSON
    }

    @GetMapping("/newcategory")
    public String newCate() {
        return "adminPage/newCategory";
    }

    @GetMapping("/commMgt")
    public String commMgt(Model model) {
        List<CategoryDto> categoryStatisticsList = categoryService.getTotal();
        model.addAttribute("categoryStatisticsList", categoryStatisticsList);
        return "adminPage/manageCommunity";
    }

    @PostMapping("/createCate")
    public String newCate(@ModelAttribute CategoryDto categoryDto) throws Exception {
        try {
            categoryService.save(categoryDto);
            return "redirect:/admin/commMgt";

        } catch (IllegalArgumentException e) {
            return "redirect:/admin/newcategory?duplicateError&categoryName=" + URLEncoder.encode(categoryDto.getCategoryName(), "UTF-8");
        }
    }

    @GetMapping("delete/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable int id) {
        try {
            categoryService.delete(id);
            return ResponseEntity.ok().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/update/{id}")
    public String categoryUpdate(@PathVariable int id, Model model) {
        Category categoryDto = categoryService.getCategoryById(id);
        model.addAttribute("categoryDto", categoryDto);
        return "adminPage/categoryUpdate";
    }

    @PostMapping("/updateCategory")
    public String categoryUpdate(@ModelAttribute CategoryDto categoryDto) throws Exception {
        try {
            categoryService.update(categoryDto);
            return "redirect:/admin/commMgt";
        } catch (IllegalArgumentException e) {
            return "redirect:/admin/update/" + categoryDto.getCategoryId()
                    + "?duplicateError&categoryName=" + URLEncoder.encode(categoryDto.getCategoryName(), "UTF-8");
        }
    }

    @PostMapping("/deleteBoard")
    public ResponseEntity<?> deleteBoard(@RequestBody List<Integer> boardIds) {
        try {
            boardService.deleteBoards(boardIds);
            return ResponseEntity.ok().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/adminUpdate/{boardId}")
    public String boardCategoryUpdate(@PathVariable int boardId,Model model){
        List<CategoryDto> categoryDtoList = categoryService.getAllCategory();
        String categoryName = boardService.getBoardById(boardId).getCategory().getCategoryName();
        model.addAttribute("categoryName", categoryName);
        model.addAttribute("boardId", boardId);
        model.addAttribute("categoryList", categoryDtoList);
        return "adminPage/adminUpdateCategory";
    }
    @Transactional
    @GetMapping("/adminUpdate2/{updateCategory}/{boardId}")
    public String changeBoardCategoryName(@PathVariable int updateCategory, @PathVariable int boardId) {
        boardService.updateCategoryNameById(boardId, updateCategory);

        return "redirect:/admin/boardMgt/0?id=0"; // 원하는 리다이렉션 경로로 변경해주세요.
    }

    //대댓글 삭제 아작스
    @PostMapping("/deleteCommentsComment")
    public ResponseEntity<?> deleteCommentsComment(@RequestBody List<Integer> commentsCommentIds) {
        try {
            commentsCommentService.deletes(commentsCommentIds);
            return ResponseEntity.ok().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    //댓글 삭제
    @PostMapping("/deleteComment")
    public ResponseEntity<?> deleteComments(@RequestBody List<Integer> commentsIds) {
        try {
            System.out.println("아이디아이디"+commentsIds);
            commentService.deletes(commentsIds);
            return ResponseEntity.ok().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    //    게시글 신고 클릭시 팝업
    @GetMapping("/boardReport/{boardId}")
    public String boardReportResult(@PathVariable int boardId,@RequestParam(defaultValue = "1") int page, Model model){
        int pageSize = 5; // 한 페이지에 보여줄 게시글 수
        ReportPageDto reportPageDto = reportService.getReportsForBoard(boardId, page, pageSize);
        model.addAttribute("reportPageDto", reportPageDto);
        model.addAttribute("boardId", boardId);

        return "adminPage/boardReportResult";
    }

    //댓글 신고 클릭시 팝업
    @GetMapping("/commentReport/{commentId}")
    public String commentReportResult(@PathVariable int commentId, @RequestParam(defaultValue = "1") int page, Model model){
        int pageSize = 5; // 한 페이지에 보여줄 게시글 수
        ReportPageDto reportPageDto = reportService.getReportsForComment(commentId, page, pageSize);
        model.addAttribute("commentId", commentId);
        model.addAttribute("reportPageDto", reportPageDto);

        return "adminPage/commentReportResult";
    }
    @GetMapping("/commentsComment/{commentsCommentId}")
    public String commentsCommentReportResult(@PathVariable int commentsCommentId, @RequestParam(defaultValue = "1") int page, Model model) {
        int pageSize = 5; // 한 페이지에 보여줄 게시글 수
        ReportPageDto reportPageDto = reportService.getReportsForCommentsComment(commentsCommentId, page, pageSize);
        model.addAttribute("commentsCommentId", commentsCommentId);
        model.addAttribute("reportPageDto", reportPageDto);

        return "adminPage/commentsCommentReportResult";
    }
    @PostMapping("/confirmReports")
    public ResponseEntity<?> confirmReports(@RequestBody List<Integer> reportIds) {
        try {
            int handledReportCount = reportService.confirmReports(reportIds);
            return ResponseEntity.ok(handledReportCount);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}