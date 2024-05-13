package kr.or.ddit.board.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import kr.or.ddit.DeleteHint;
import kr.or.ddit.board.service.IBoardReplyService;
import kr.or.ddit.common.InsertHint;
import kr.or.ddit.enumpkg.ServiceResult;
import kr.or.ddit.vo.PagingVO;
import kr.or.ddit.vo.ReplyVO;

@Controller
@RequestMapping("/reply")
public class ReplyController {
	@Inject
	IBoardReplyService service;
	
	@RequestMapping(value="replyDelete.do", method=RequestMethod.POST)
	public String replyDelete(
			@Validated(DeleteHint.class) @ModelAttribute("reply") ReplyVO reply
			, Model model
			){
		String msg = null;
		String view = null;
		
		Map<String, Object> errors = new HashMap<String, Object>();
		
		ServiceResult result = service.removeReply(reply);
		if(ServiceResult.OK.equals(result)) {
			view = "redirect:/reply/replyList.do?bo_no="+reply.getBo_no();
		}else {
			model.addAttribute("success", "false");
			model.addAttribute("message", "비번 오류, 다시 시도");
			view = "jsonView";
		}
		
		return view;
	}
	
	@RequestMapping(value="replyInsert.do", method=RequestMethod.POST)
	public String replyInsert(
			@Validated(InsertHint.class) @ModelAttribute("reply") ReplyVO reply 
			, Errors errors
			, Model model
			) throws IOException {
		String msg = null;
		String view = null;
		
		if (!errors.hasErrors()) {
			ServiceResult result = service.createReply(reply);
			if(ServiceResult.OK.equals(result)) {
				view = "redirect:/reply/replyList.do?bo_no="+reply.getBo_no();
			}else {
				model.addAttribute("success", "false");
				model.addAttribute("message", "서버 에러, 다시 시도");
				view = "jsonView";
			}
		} else {
			model.addAttribute("success", "false");
			model.addAttribute("message", "검증실패");	
			view = "jsonView";
		}
		return view;
	}

	
	@RequestMapping(value="replyList.do", produces="application/json;charset=UTF-8")
	@ResponseBody
	public PagingVO<ReplyVO> replyList(
			@RequestParam(name="page", required=false, defaultValue="1") long currentPage
			, @RequestParam int bo_no
			){

		PagingVO<ReplyVO> pagingVO = new PagingVO<>(5,3);
		ReplyVO searchVO = new ReplyVO();
		searchVO.setBo_no(bo_no);
		pagingVO.setSearchData(searchVO);
		
		pagingVO.setCurrentPage(currentPage);
		long totalRecord = service.retrieveReplyCount(pagingVO);
		pagingVO.setTotalRecord(totalRecord);
		
		List<ReplyVO> replyList = service.retrieveReplyList(pagingVO);
		pagingVO.setDataList(replyList);

		return pagingVO;
	}
}


