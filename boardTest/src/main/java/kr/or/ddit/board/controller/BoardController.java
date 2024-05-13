package kr.or.ddit.board.controller;

import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import kr.or.ddit.board.service.IBoardService;
import kr.or.ddit.common.InsertHint;
import kr.or.ddit.common.UpdateHint;
import kr.or.ddit.enumpkg.ServiceResult;
import kr.or.ddit.vo.BoardVO;
import kr.or.ddit.vo.PagingVO;

@Controller
@RequestMapping("/board")
public class BoardController {
	@Inject
	IBoardService service;
	
	@RequestMapping(produces="application/json;charset=UTF-8")
	@ResponseBody
	public PagingVO<BoardVO> ajaxList(
			@RequestParam(name="page", required=false, defaultValue="1") long currentPage, @ModelAttribute("pagingVO") PagingVO<BoardVO> pagingVO, Model model , HttpServletResponse resp){
		boardList(currentPage, pagingVO, model, resp);
		return pagingVO;
	}
	
	@GetMapping(value="/{bo_no}")
	public String boardView(@PathVariable int bo_no, Model model){
		BoardVO board =  service.retrieveBoard(bo_no);
		model.addAttribute("board", board);
		
		String view = "board/boardView";
		return view;
	}
	
	@GetMapping(value="/boardInsert")
	public String get(HttpServletRequest req, HttpServletResponse resp) {
		return "board/boardForm";
	}
	
	@RequestMapping(value="boardUpdateForm", method=RequestMethod.POST)
	public String updateForm(@RequestParam(required=true) int bo_no, @RequestParam(required=true) String bo_pass, Model model, RedirectAttributes redirectAttributes) {
		BoardVO inputBoard = new BoardVO();
		inputBoard.setBo_pass(bo_pass);
		service.encryptPassword(inputBoard);
		BoardVO board = service.retrieveBoard(bo_no);
		String view = null;
		if(board.getBo_pass().equals(inputBoard.getBo_pass())) {
			model.addAttribute("board", board);
			view = "board/boardForm";
		}else {
			redirectAttributes.addFlashAttribute("message", "비번 오류");
			view = "redirect:/board"+bo_no;
		}
		return view;
	}
	
	
	@GetMapping
	public String boardList(@RequestParam(name="page", required=false, defaultValue="1") long currentPage, @ModelAttribute("pagingVO") PagingVO<BoardVO> pagingVO, Model model , HttpServletResponse resp){
		pagingVO.setCurrentPage(currentPage);
		
		long totalRecord = service.retrieveBoardCount(pagingVO);
		pagingVO.setTotalRecord(totalRecord);
		
		List<BoardVO> boardList = service.retrieveBoardList(pagingVO);
		pagingVO.setDataList(boardList);
		
		resp.setHeader("Pragma", "no-cache");
		resp.setHeader("Cache-Control", "no-cache");
		resp.addHeader("Cache-Control", "no-store");
		resp.setDateHeader("Expires", 0);
		
		model.addAttribute("pagingVO", pagingVO);
		return "board/boardList";
	}
	
	@PostMapping
	public String boardInsert(@Validated(InsertHint.class) @ModelAttribute("board") BoardVO board, Errors errors, Model model){
		String view = null;
		String msg = null;
		if (!errors.hasErrors()) {
			ServiceResult result = service.createBoard(board);
			if (ServiceResult.OK.equals(result)) {
				view = "redirect:/board/" + board.getBo_no();
			} else {
				view = "board/boardForm";
				msg = "서버 오류, 다시 시도.";
			}
		} else {
			view = "board/boardForm";
		}

		model.addAttribute("message", msg);
		return view;
	}
	
	@PutMapping(value="/{bo_no}")
	public String update(@Validated(UpdateHint.class) @ModelAttribute("board") BoardVO board, BindingResult errors, Model model){
		String view = null;
		String msg = null;
		if (!errors.hasErrors()) {
			ServiceResult result = service.modifyBoard(board);
			if (ServiceResult.OK.equals(result)) {
				view = "redirect:/board/" + board.getBo_no();
			} else if(ServiceResult.INVALIDPASSWORD.equals(result)) {
				view = "board/boardForm";
				msg = "비번 오류, 다시 시도.";
			}else {
				view = "board/boardForm";
				msg = "서버 오류, 다시 시도.";
			}// result if end
		} else {
			view = "board/boardForm";
		}

		model.addAttribute("message", msg);
		
		return view;
	}
	
	@PostMapping(value="/{bo_no}")
	public String delete(@RequestParam(required=true) int bo_no, @RequestParam(required=true) String bo_pass, RedirectAttributes redirectAttributes) {
		ServiceResult result = service.removeBoard(new BoardVO(bo_no, bo_pass));
		String view = "redirect:/board/"+bo_no;
		String message = null;
		switch (result) {
			case INVALIDPASSWORD:
				message = "비번 오류";
				break;
			case FAILED:
				message = "서버 오류";
				break;
			default:
				view = "redirect:/board";
				break;
		}
		redirectAttributes.addFlashAttribute("message", message);
		return view;
	}
}
















