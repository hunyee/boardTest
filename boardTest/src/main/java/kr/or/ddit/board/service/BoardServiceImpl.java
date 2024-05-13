package kr.or.ddit.board.service;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import kr.or.ddit.board.dao.IBoardDAO;
import kr.or.ddit.board.dao.IPdsDAO;
import kr.or.ddit.enumpkg.ServiceResult;
import kr.or.ddit.exception.CommonException;
import kr.or.ddit.utils.EncryptUtils;
import kr.or.ddit.vo.BoardVO;
import kr.or.ddit.vo.PagingVO;
import kr.or.ddit.vo.PdsVO;

@Service
public class BoardServiceImpl implements IBoardService {
	@Inject
	IBoardDAO boardDAO;
	@Inject
	IPdsDAO pdsDAO;
	
	@Inject
	WebApplicationContext container;
	
	@Value("#{appInfo['pdsPath']}")
	String pdsPath;
	
	public void encryptPassword(BoardVO board){
		String plain = board.getBo_pass();
		
		if(StringUtils.isBlank(plain)) return;
		
		String encoded = EncryptUtils.encryptSha512Base64(plain);
		board.setBo_pass(encoded);
	}
	
	public void preProcessPdsList(BoardVO board){
		List<PdsVO> pdsList = board.getPdsList();
		if(pdsList==null) return;
		
		for(PdsVO pds : pdsList) {
			String saveName = UUID.randomUUID().toString();
			File saveFile = new File(pdsPath, saveName);
			pds.setPds_savepath(saveFile.getAbsolutePath());
		} 
	}
	
	private void processPds(BoardVO board){
		preProcessPdsList(board);
		
		// 신규 첨부파일에 대한 처리
		List<PdsVO> pdsList = board.getPdsList();
		if(pdsList!=null && pdsList.size()>0) {
			pdsDAO.insertPdsAll(board);
			
			for(PdsVO pds : pdsList) {
				String savePath = pds.getPds_savepath();
				MultipartFile item = pds.getFileItem();
				try(
						InputStream is = item.getInputStream();
						){
					FileUtils.copyInputStreamToFile(is, new File(savePath));
				}catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}
		// 삭제 파일 처리
		int[] deletePdsNos = board.getDeletePdsNos();
		if(deletePdsNos!=null) {
			 // 삭제 대상 첨부파일 조회
			 List<PdsVO> delPdsList = pdsDAO.selectPdsList(board);
			 // 삭제
			 pdsDAO.deletePdsAll(board);
			 
			 // 실제 파일 삭제
			 deleteRealPds(delPdsList);
		}
	}
	
	private void deleteRealPds(List<PdsVO> pdsList) {
		if(pdsList!=null) {
			for(PdsVO pds : pdsList) {
				if(pds==null) continue;
				String savedPath = pds.getPds_savepath();
				if(savedPath!=null){
					FileUtils.deleteQuietly(new File(savedPath));
				}
			}
		} // if(pdsList!=null) end
	}
	
	@Transactional
	// 현재 트랜잭션관리 안됨. 폭망!!!했음. ㅠ.ㅠ
	@Override
	public ServiceResult createBoard(BoardVO board) {
		encryptPassword(board);
		int rowCnt = boardDAO.insertBoard(board);
		
		ServiceResult result = ServiceResult.FAILED;
		if(rowCnt > 0) {
			//if(1==1)
			//	throw new RuntimeException("트랜잭션 관리 여부 확인.");
			processPds(board);	
			
			result = ServiceResult.OK;
		} // if(rowCnt > 0) end
		return result;
	}

	@Override
	public List<BoardVO> retrieveBoardList(PagingVO<BoardVO> pagingVO) {
		return boardDAO.selectBoardList(pagingVO);
	}

	@Override
	public long retrieveBoardCount(PagingVO<BoardVO> pagingVO) {
		return boardDAO.selectBoardCount(pagingVO);
	}

	@Override
	public BoardVO retrieveBoard(int bo_no) {
		boardDAO.incrementHit(bo_no);
		
		BoardVO board = boardDAO.selectBoard(bo_no);
		if(board == null) throw new CommonException(bo_no+"번 글이 없음.");		
		
		return board;
	}

	@Override
	public ServiceResult singo(int bo_no) {
		int rowCnt = boardDAO.incrementReport(bo_no);
		ServiceResult result = ServiceResult.FAILED;
		if(rowCnt>0) result = ServiceResult.OK;
		return result;
	}

	@Override
	public ServiceResult likeOrHate(BoardVO board) {
		int rowCnt = boardDAO.incrementLikeOrHate(board);
		ServiceResult result = ServiceResult.FAILED;
		if(rowCnt>0) result = ServiceResult.OK;
		return result;
	}

	@Transactional
	@Override
	public ServiceResult modifyBoard(BoardVO board) {
		encryptPassword(board);
		BoardVO savedBoard = boardDAO.selectBoard(board.getBo_no());
		if(savedBoard==null) throw new CommonException(board.getBo_no()+"번 글이 없음.");
		ServiceResult result = ServiceResult.INVALIDPASSWORD;
		if(savedBoard.getBo_pass().equals(board.getBo_pass())){
			result = ServiceResult.FAILED;
			int rowCnt = boardDAO.updateBoard(board);
			if(rowCnt > 0) {
				// 업로드 파일에 대한 처리
				// 삭제 파일에 대한 처리
				processPds(board);
				
				result = ServiceResult.OK;
			}				
		}
		return result;
	}

	@Transactional
	@Override
	public ServiceResult removeBoard(BoardVO board) {
		encryptPassword(board);
		BoardVO savedBoard = boardDAO.selectBoard(board.getBo_no());
		if(savedBoard==null) throw new CommonException(board.getBo_no()+"번 글이 없음.");
		ServiceResult result = null;
		if(savedBoard.getBo_pass().equals(board.getBo_pass())){
			int rowCnt = boardDAO.deleteBoard(board.getBo_no());
			if(rowCnt > 0) {
				List<PdsVO> pdsList = savedBoard.getSavedPdsList();
				
				deleteRealPds(pdsList);
				
				result = ServiceResult.OK;
			}else {
				result = ServiceResult.FAILED;
			}// if(rowCnt > 0) end				
		}else {
			result = ServiceResult.INVALIDPASSWORD;
		}
		return result;
	}

	@Override
	public PdsVO downloadPds(int pds_no) {
		PdsVO pds = pdsDAO.selectPds(pds_no);
		if(pds==null) throw new CommonException(pds_no+" 파일이 엄슴. ");
		return pds;
	}

}