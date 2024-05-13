package kr.or.ddit.board.service;

import java.util.List;

import kr.or.ddit.enumpkg.ServiceResult;
import kr.or.ddit.vo.PagingVO;
import kr.or.ddit.vo.ReplyVO;

public interface IBoardReplyService {
	/**
	 * 댓글 쓰기
	 * @param reply
	 * @return
	 */
	public ServiceResult createReply(ReplyVO reply);
	/**
	 * 댓글 목록 조회(비동기에 사용)
	 * @param pagingVO
	 * @return
	 */
	public List<ReplyVO> retrieveReplyList(PagingVO<ReplyVO> pagingVO);
	/**
	 * 게시글에 소속된 총 댓글수 조회
	 * @param pagingVO
	 * @return
	 */
	public long  retrieveReplyCount(PagingVO<ReplyVO> pagingVO);
	/**
	 * 댓글 삭제
	 * @param reply
	 * @return
	 */
	public ServiceResult removeReply(ReplyVO reply);
}
