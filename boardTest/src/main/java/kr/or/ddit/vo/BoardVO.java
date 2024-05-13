package kr.or.ddit.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

import kr.or.ddit.common.UpdateHint;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@EqualsAndHashCode(of="bo_no")
public class BoardVO implements Serializable{
	
	public BoardVO(Integer bo_no, String bo_pass) {
		super();
		this.bo_no = bo_no;
		this.bo_pass = bo_pass;
	}
	
	private Integer startPdsNo;
	private Long rnum;
	@NotNull(groups=UpdateHint.class)
	private Integer bo_no;
	@NotBlank
	private String bo_title;
	@NotBlank
	private String bo_writer;
	@NotBlank
	private String bo_pass;
	private String bo_date;
	private String bo_content;
	@NotBlank
	private String code_id;
	private String code_name;
	private Integer bo_hit;
	private Integer bo_report;
	private Integer bo_like;
	private Integer bo_hate;
	@NotBlank
	private String bo_ip;
	private String bo_mail;
	private Integer bo_parent;
	private boolean likeFlag;
	
	private int[] deletePdsNos;
	
	private List<ReplyVO> replyList;
	private List<PdsVO> pdsList;
	private List<PdsVO> savedPdsList;
	
	private MultipartFile[] bo_files;
	
	public void setBo_files(MultipartFile[] bo_files) {
		if(bo_files==null) return;
		this.bo_files = bo_files;
		
		this.pdsList = new ArrayList<>();
		for(MultipartFile file : bo_files){
			if(StringUtils.isBlank(file.getOriginalFilename())) continue;
			pdsList.add(new PdsVO(file));
		}
	}
}





