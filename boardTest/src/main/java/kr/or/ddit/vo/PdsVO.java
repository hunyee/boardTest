package kr.or.ddit.vo;

import java.io.Serializable;

import org.apache.commons.io.FileUtils;
import org.springframework.web.multipart.MultipartFile;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class PdsVO implements Serializable{
	
	public PdsVO(MultipartFile fileItem) {
		super();
		this.fileItem = fileItem;
		pds_filename = fileItem.getOriginalFilename();
		pds_size = fileItem.getSize();
		pds_fancysize = FileUtils.byteCountToDisplaySize(fileItem.getSize());
		pds_mime = fileItem.getContentType();
	}
	private Integer pds_no;
	private Integer bo_no;
	private String pds_filename;
	private String pds_savepath;
	private String pds_mime;
	private Long pds_size;
	private String pds_fancysize;
	
	private MultipartFile fileItem;
}
