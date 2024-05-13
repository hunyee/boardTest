package kr.or.ddit.board.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import kr.or.ddit.vo.BoardVO;
import kr.or.ddit.vo.PagingVO;

@Repository
public interface IBoardDAO {
	public int insertBoard(BoardVO board);
	
	public List<BoardVO> selectBoardList(PagingVO<BoardVO> pagingVO);
	public long selectBoardCount(PagingVO<BoardVO> pagingVO);
	public BoardVO selectBoard(int bo_no);
	public int incrementHit(int bo_no);
	public int incrementReport(int bo_no);
	public int incrementLikeOrHate(BoardVO board);
	
	public int updateBoard(BoardVO board);
	
	public int deleteBoard(int bo_no);
}















