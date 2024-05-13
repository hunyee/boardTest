package kr.or.ddit.vo;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

import kr.or.ddit.DeleteHint;
import kr.or.ddit.common.InsertHint;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * REPLYBOARD 테이블 대상
 *
 */
@Data
@EqualsAndHashCode(of="rep_no")
public class ReplyVO implements Serializable{
	@NotNull(groups=DeleteHint.class)
	private Integer rep_no;
	@NotNull
	private Integer bo_no;
	@NotBlank(groups=InsertHint.class)
	private String rep_writer;
	@NotBlank
	private String rep_pass;
	@NotBlank(groups=InsertHint.class)
	private String rep_ip;
	@NotBlank(groups=InsertHint.class)
	private String rep_content;
	private String rep_date;
}





