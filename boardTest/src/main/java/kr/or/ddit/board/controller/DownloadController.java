package kr.or.ddit.board.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import kr.or.ddit.board.service.IBoardService;
import kr.or.ddit.enumpkg.BrowserType;
import kr.or.ddit.vo.PdsVO;

@Controller
public class DownloadController {
	
	@Inject
	IBoardService service;
	
	@RequestMapping("/board/download.do")
	public String download(
			@RequestParam(required=true, name="what") int pds_no
			, @RequestHeader(name="User-Agent") String userAgent
			, HttpServletResponse resp
			) throws IOException {
		PdsVO pds = service.downloadPds(pds_no);
		String savePath = pds.getPds_savepath();
		String filename = pds.getPds_filename();
//		String userAgent = req.getHeader("User-Agent");
		
		BrowserType brType = BrowserType.matchedType(userAgent);
		if(BrowserType.IE.equals(brType) || BrowserType.TRIDENT.equals(brType)) {
			filename = URLEncoder.encode(filename, "UTF-8");
		}else {
			filename = new String(filename.getBytes(), "ISO-8859-1");
		}
		
		File saveFile = new File(savePath);
		
		if(!saveFile.exists()) {
			resp.sendError(404);
			return null;
		}
		
		resp.setHeader("Content-Disposition", "attachment;filename=\""+filename+"\"");
		
		try(
			InputStream is = new FileInputStream(saveFile);
			OutputStream os = resp.getOutputStream();
		){
			IOUtils.copy(is, os);
		}
		return null;
	}
}







