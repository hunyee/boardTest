package kr.or.ddit.board.service;

import java.util.List;

import kr.or.ddit.enumpkg.ServiceResult;
import kr.or.ddit.vo.BoardVO;
import kr.or.ddit.vo.PagingVO;
import kr.or.ddit.vo.PdsVO;

public interface IBoardService {
	public void encryptPassword(BoardVO board);
	/**
	 * 게시글 작성, 첨부파일에 대한 처리 필요
	 * @param board
	 * @return OK / FAIL
	 */
	public ServiceResult createBoard(BoardVO board);
	public List<BoardVO> retrieveBoardList(PagingVO<BoardVO> pagingVO);
	public long retrieveBoardCount(PagingVO<BoardVO> pagingVO);
	/**
	 * 게시글 조회
	 * @param bo_no
	 * @return 조회 성공시 조회수 증가, 존재하지 않으면,CommonException 발생
	 */
	public BoardVO retrieveBoard(int bo_no); // 조회수 증가 필요
	/**
	 * 게시글 신고수 증가
	 * @param bo_no
	 * @return
	 */
	public ServiceResult singo(int bo_no);
	/**
	 * 게시글 추천/비추천 수 증가
	 * likeFlag : true(추천), false(비추천)
	 * @param board
	 * @return
	 */
	public ServiceResult likeOrHate(BoardVO board);
	/**
	 * 게시글 수정, 첨부파일에 대한 처리 필요
	 * @param board
	 * @return CommonException/INVALIDPASSWORD/OK/FAIL
	 */
	public ServiceResult modifyBoard(BoardVO board);
	/**
	 * 게시글 삭제
	 * @param board
	 * @return CommonException/INVALIDPASSWORD/OK/FAIL
	 */
	public ServiceResult removeBoard(BoardVO board);
	/**
	 * 첨부파일 다운로드
	 * @param pds_no
	 * @return 파일이 없는 경우, CommonException 발생
	 */
	public PdsVO downloadPds(int pds_no); // 다운로드용
}
