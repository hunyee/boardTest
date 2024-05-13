package kr.or.ddit.board.controller;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.WebApplicationContext;

import kr.or.ddit.board.service.IBoardService;
import kr.or.ddit.enumpkg.ServiceResult;
import kr.or.ddit.utils.CookieUtil;
import kr.or.ddit.utils.CookieUtil.TextType;
import kr.or.ddit.vo.BoardVO;

@Controller
public class BoardOthersController {
	@Inject
	IBoardService service;
	
	@Inject
	WebApplicationContext  container;

	@RequestMapping(value="/board/boardReport.do", produces="text/plain;charset=UTF-8")
	@ResponseBody
	public String singo(
			@RequestParam(required=true, name="what") int bo_no
			, @CookieValue(name="singoCookie", required=false) String cookieValue
			, HttpServletResponse resp
			) {
		ServiceResult result = service.singo(bo_no);
		String message;
		if(ServiceResult.OK.equals(result)) {
			message = "SUCCESS";
//			String cookieValue = new CookieUtil(req).getCookieValue("singoCookie");
			if(StringUtils.isBlank(cookieValue)) {
				cookieValue = bo_no+"";
			}else {
				if(!cookieValue.contains(bo_no+""))
						cookieValue += ","+bo_no+""; 
			} 
			
			Cookie singoCookie = CookieUtil.createCookie("singoCookie", cookieValue, 
						container.getServletContext().getContextPath(), TextType.PATH, 60*60*24*7);
			resp.addCookie(singoCookie);
		}else {
			message = "FAIL";
		}
		return message;
	}
	
	@RequestMapping(value="/board/like.do", produces="text/plain;charset=UTF-8")
	@ResponseBody
	public String likeOrHate(
			@RequestParam(required=true, name="what") int bo_no
			, @RequestParam(required=true) String type
			, @CookieValue(name="likeCookie", required=false) String cookieValue
			, HttpServletResponse resp
			) throws IOException{
//		?type=LIKE/HATE
		if(!(StringUtils.equals(type, "LIKE") 
			  || StringUtils.equals(type, "HATE"))) {
			resp.sendError(400);
			return null;
		}
		
		BoardVO board = new BoardVO();
		board.setBo_no(bo_no);
		board.setLikeFlag("LIKE".equals(type));
		ServiceResult result = service.likeOrHate(board);
		String message; 
		if(ServiceResult.OK.equals(result)) {
			message = "SUCCESS";
			if(StringUtils.isBlank(cookieValue)) {
				cookieValue = bo_no+"";
			}else {
				if(!cookieValue.contains(bo_no+""))
					cookieValue += ","+bo_no+"";  
			} 
			
			Cookie likeCookie = CookieUtil.createCookie("likeCookie", cookieValue, 
					container.getServletContext().getContextPath(), TextType.PATH, 60*60*24*7);
			resp.addCookie(likeCookie);
		}else {
			message = "FAIL";
		}
		return message;
	}
	
}
