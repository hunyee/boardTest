package kr.or.ddit.board.service;

import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import kr.or.ddit.board.dao.IReplyDAO;
import kr.or.ddit.enumpkg.ServiceResult;
import kr.or.ddit.utils.EncryptUtils;
import kr.or.ddit.vo.PagingVO;
import kr.or.ddit.vo.ReplyVO;

@Service
public class BoardReplyServiceImpl implements IBoardReplyService {
	@Inject
	IReplyDAO replyDAO;
	
	private void encryptPassword(ReplyVO reply){
		String plain = reply.getRep_pass();
		
		if(StringUtils.isBlank(plain)) return;
		
		String encoded = EncryptUtils.encryptSha512Base64(plain);
		reply.setRep_pass(encoded);
	}
	
	@Override
	public ServiceResult createReply(ReplyVO reply) {
		encryptPassword(reply);
		int rowCnt = replyDAO.insertReply(reply);
		ServiceResult result = ServiceResult.FAILED;
		if(rowCnt > 0) result = ServiceResult.OK;
		return result;
	}

	@Override
	public List<ReplyVO> retrieveReplyList(PagingVO<ReplyVO> pagingVO) {
		return replyDAO.selectReplyList(pagingVO);
	}

	@Override
	public long retrieveReplyCount(PagingVO<ReplyVO> pagingVO) {
		return replyDAO.selectReplyCount(pagingVO);
	}

	@Override
	public ServiceResult removeReply(ReplyVO reply) {
		encryptPassword(reply);
		int rowCnt = replyDAO.deleteReply(reply);
		ServiceResult result = ServiceResult.INVALIDPASSWORD;
		if(rowCnt>0) result = ServiceResult.OK;
		return result;
	}
}




