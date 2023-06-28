package com.blue.bluearchive.board.service;

import com.blue.bluearchive.admin.dto.AdminBoardDto;
import com.blue.bluearchive.admin.entity.Category;
import com.blue.bluearchive.admin.repository.CategoryRepository;
import com.blue.bluearchive.board.dto.BoardDto;
import com.blue.bluearchive.board.dto.BoardFormDto;
import com.blue.bluearchive.board.dto.BoardImgDto;
import com.blue.bluearchive.board.dto.BoardSearchDto;
import com.blue.bluearchive.board.entity.Board;
import com.blue.bluearchive.board.entity.BoardImg;
import com.blue.bluearchive.board.entity.Comment;
import com.blue.bluearchive.board.entity.CommentsComment;
import com.blue.bluearchive.board.repository.BoardRepository;
import com.blue.bluearchive.board.repository.CommentRepository;
import com.blue.bluearchive.board.repository.CommentsCommentRepository;
import com.blue.bluearchive.board.repository.formRepository.BoardImgRepository;
import com.blue.bluearchive.report.repository.ReportBoardRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final BoardImgRepository boardImgRepository;
    private final BoardImgService boardImgService;
    private final ModelMapper modelMapper;
    private final ReportBoardRepository reportBoardRepository;
    private final CommentRepository commentRepository;
    private final CommentsCommentRepository commentsCommentRepository;


    public List<BoardDto> getAllBoards() {
        List<Board> boardEntities = boardRepository.findAll();
        List<BoardDto> BoardDtos = new ArrayList<>();
        for (Board board : boardEntities) {
            BoardDtos.add(modelMapper.map(board, BoardDto.class));
        }
        return BoardDtos;
    }

    public List<BoardDto> getBoardsByCategory(Category category) {
        List<Board> boardEntities = boardRepository.findByCategory(category);
        List<BoardDto> boardDtos = new ArrayList<>();
        for (Board board : boardEntities) {
            boardDtos.add(modelMapper.map(board, BoardDto.class));
        }
        return boardDtos;
    }
    public BoardDto getBoardById(int boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new NoSuchElementException("Board not found"));
        return modelMapper.map(board, BoardDto.class);
    }
    public BoardFormDto getBoardImgById(int boardId){
        List<BoardImg> boardImgList=boardImgRepository.findByBoardBoardIdOrderByBoardImgIdAsc(boardId);
        List<BoardImgDto> boardImgDtoList = new ArrayList<>();
        for(BoardImg boardImg : boardImgList){
            BoardImgDto boardImgDto = BoardImgDto.of(boardImg);
            boardImgDtoList.add(boardImgDto);
        }
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new NoSuchElementException("Board not found"));
        BoardFormDto boardFormDto = BoardFormDto.of(board);
        boardFormDto.setBoardImgDtoList(boardImgDtoList);
        return boardFormDto;
    }

        public void updateCommentCount(Board boardDto) {
            // 엔티티를 업데이트하는 로직을 추가합니다.
            Board board = boardRepository.findById(boardDto.getBoardId()).orElse(null);
            if (board != null) {
                // 엔티티의 필드를 업데이트합니다.
                board.setCommentCount(boardDto.getCommentCount());
                // Repository를 통해 엔티티를 저장 또는 업데이트합니다.
                boardRepository.save(board);
            }
        }
        public Integer updateBoard(BoardFormDto boardFormDto,List<String>boardImgUrlList,List<MultipartFile> boardImgFileList)throws Exception{

        Board board = boardRepository.findById(boardFormDto.getBoardId()).orElseThrow(EntityNotFoundException::new);
             board.updateBoard(boardFormDto);
             boardRepository.save(board);
             List<BoardImg> boardImg = boardImgRepository.findByBoardBoardIdOrderByBoardImgIdAsc(boardFormDto.getBoardId());
            List<String>boardImgUrl = new ArrayList<>();
            for(BoardImg imgUrl:boardImg){
                boardImgUrl.add(imgUrl.getBoardImgUrl());
            }

            List<String> imagesToDelete = new ArrayList<>();
            for (String imgUrl : boardImgUrl) {
                if (!boardImgUrlList.contains(imgUrl)) {
                    imagesToDelete.add(imgUrl);
                    System.out.println(imgUrl);
                    System.out.println("여깅");
                }
            }


            for (String imageUrl : imagesToDelete) {
                BoardImg deletedImg = boardImgRepository.findByBoardImgUrl(imageUrl);

                if (deletedImg != null) {
                    System.out.println(deletedImg.getBoardImgId());
                    // 필요한 작업 수행
                    boardImgRepository.delete(deletedImg);
                }

            }



            if (boardImgFileList != null) {
               saveBoardImages(board,boardImgFileList);
            }


        return board.getBoardId();
        }

    public void incrementBoardCount(int boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new NoSuchElementException("Board not found"));
        if (board != null) {
            board.setBoardCount(board.getBoardCount() + 1);
            boardRepository.save(board);
        }
    }
    public void incrementBoardLikeCount(int boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new NoSuchElementException("Board not found"));
        if (board != null) {
            board.setBoardLikeCount(board.getBoardLikeCount() + 1);
            boardRepository.save(board);
        }
    }
    public void incrementBoardHateCount(int boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new NoSuchElementException("Board not found"));
        if (board != null) {
            board.setBoardHateCount(board.getBoardHateCount() + 1);
            boardRepository.save(board);
        }
    }
    public void decreaseBoardLikeCount(int boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new NoSuchElementException("Board not found"));
        if (board != null) {
            board.setBoardLikeCount(board.getBoardLikeCount() - 1);
            boardRepository.save(board);
        }
    }
    public void decreaseBoardHateCount(int boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new NoSuchElementException("Board not found"));
        if (board != null) {
            board.setBoardHateCount(board.getBoardHateCount() - 1);
            boardRepository.save(board);
        }
    }


    public Integer saveBoard(BoardFormDto boardFormDto, List<MultipartFile> boardImgFileList) throws Exception {
        Board board = boardFormDto.createBoard();
        boardRepository.save(board);

        if (boardImgFileList != null) {
            saveBoardImages(board, boardImgFileList);
        }

        return board.getBoardId();
    }
    public void saveBoardImages(Board board, List<MultipartFile> boardImgFileList) throws Exception {
        for (int i = 0; i < boardImgFileList.size(); i++) {
            BoardImg boardImg = new BoardImg();
            boardImg.setBoard(board);
            
            boardImgService.saveBoardImg(boardImg, boardImgFileList.get(i));
        }
            
    }
    public int getBoardLikeCount(int boardId) {
        Board board = boardRepository.findById(boardId).orElse(null);
        return (board != null) ? board.getBoardLikeCount() : 0;
    }
    public int getBoardHateCount(int boardId) {
        Board board = boardRepository.findById(boardId).orElse(null);
        return (board != null) ? board.getBoardHateCount() : 0;
    }

    @Transactional(readOnly = true)
    public Page<Board> getBoardPage(BoardSearchDto boardSearchDto, Pageable pageable){
        return boardRepository.getBoardPage(boardSearchDto,pageable);
    }

    @Transactional(readOnly = true)
    public Page<Board> getCategoryBoardPage(Category category,BoardSearchDto boardSearchDto, Pageable pageable){
        return boardRepository.getCategoryBoardPage(category,boardSearchDto,pageable);
    }
    //승훈 추가
    // 승훈 추가 내역
    @Autowired
    private CategoryRepository categoryRepository;

    public List<BoardDto> getBoardsByCategoryId(int categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NoSuchElementException("Category not found"));
        List<Board> boardEntities = boardRepository.findByCategory(category);
        List<BoardDto> boardDtos = new ArrayList<>();
        for (Board board : boardEntities) {
            boardDtos.add(modelMapper.map(board, BoardDto.class));
        }
        return boardDtos;
    }
    public void delete(int boardId) {
        // 게시물 삭제
        boardRepository.deleteById(boardId);
    }
    public Page<AdminBoardDto> searchBoards(String option, String keyword, Pageable pageable) {
        Page<Board> boardEntities;
        List<AdminBoardDto> boardDtos = new ArrayList<>();

        switch (option) {
            case "1": // 제목
                boardEntities = boardRepository.findByBoardTitleContaining(keyword, pageable);
                break;
            case "2": // 작성자
                boardEntities = boardRepository.findByCreatedByContaining(keyword, pageable);
                break;
            default:
                boardEntities = Page.empty();
                break;
        }

        for (Board board : boardEntities) {
            boardDtos.add(modelMapper.map(board, AdminBoardDto.class));
            int size = reportBoardRepository.findByBoardBoardIdAndReportStatusFalse(board.getBoardId()).size();
            boardDtos.get(boardDtos.size() - 1).setNotReportCount(size);
        }

        return new PageImpl<>(boardDtos, pageable, boardEntities.getTotalElements());
    }
    public void deleteBoards(List<Integer> boardIds) {
        for (Integer boardId : boardIds) {
            Board board = boardRepository.findById(boardId).orElseThrow(NoSuchElementException::new);

            List<Comment> comments = commentRepository.findByBoard(board);
            for (Comment comment : comments) {
                commentRepository.delete(comment);
            }
            // 댓글 삭제
            commentRepository.deleteAll(comments);

            // 게시글 삭제
            boardRepository.delete(board);
        }
    }
    //게시글 수정 부분 코드추가
    @Transactional
    public void updateCategoryNameById(int boardId, int categoryId) {
        boardRepository.updateCategoryNameById(boardId, categoryId);
    }





}


