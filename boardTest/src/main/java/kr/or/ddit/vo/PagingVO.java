package kr.or.ddit.vo;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 페이징 처리에 필요한 프로퍼티를 가진 VO
 *
 */
@Getter
@NoArgsConstructor
public class PagingVO<T> {
	
	public PagingVO(int screenSize, int blockSize) {
		super();
		this.screenSize = screenSize;
		this.blockSize = blockSize;
	}

	private long totalRecord; // 조회로 결정
	private int screenSize = 10;
	private int blockSize = 5;
	private long currentPage; // 파라미터로 결정
	private long startRow;
	private long endRow;
	private long startPage;
	private long endPage;
	private long totalPage;
	
	// 검색 조건과 키워드
	private String searchType;
	private String searchWord;
	
	private List<T> dataList;
	// 도메인에 검색 조건을 다르게 처리하기 위해
	private T searchData;
	
	public void setDataList(List<T> dataList) {
		this.dataList = dataList;
	}
	public void setSearchType(String searchType) {
		this.searchType = searchType;
	}
	public void setSearchWord(String searchWord) {
		this.searchWord = searchWord;
	}
	public void setSearchData(T searchData) {
		this.searchData = searchData;
	}
	
	/**
	 * totalRecord 가 결정되면, totalPage 까지 결정됨.
	 * @param totalRecord
	 */
	public void setTotalRecord(long totalRecord) {
		this.totalRecord = totalRecord;
		totalPage = (totalRecord + (screenSize - 1))/screenSize;
	}
	
	/**
	 * currentPage 가 결정되면, startRow/endRow/startPage/endPage 가 결정됨.
	 * @param currentPage
	 */
	public void setCurrentPage(long currentPage) {
		this.currentPage = currentPage;
		endRow = screenSize * currentPage;
		startRow = endRow - (screenSize - 1);
		endPage = (currentPage + (blockSize -1))/blockSize * blockSize;
		startPage = endPage - (blockSize - 1);
//		int middle = (int) Math.ceil(blockSize/(double)2);
//		startPage = currentPage - (middle-1);
//		if(startPage<1) startPage = 1;
//		endPage = startPage + (blockSize - 1);
	}
	
	private String functionName = "paging";
	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}
	
	public String getPagingHTML(){
		String pattern = ""+
				"<li class='page-item %s'>\n"
				+"<a class='page-link' style='cursor:pointer;' onclick='"+functionName+"(%d);'>%s</a>\n"
				+"</li>\n";
		StringBuffer pagingHTML = new StringBuffer();
		pagingHTML.append("<nav>");
		pagingHTML.append("<ul class='pagination  justify-content-center'>");
		
		
		pagingHTML.append(String.format(pattern, startPage==1?"disabled":"", (startPage-1),"이전"));
		long last = endPage<totalPage?endPage:totalPage;
				
		for(long idx=startPage; idx<=last; idx++){
			pagingHTML.append(String.format(pattern, idx==currentPage?"active":"" , idx, idx));
		}
		pagingHTML.append(String.format(pattern, endPage>=totalPage?"disabled":"",(endPage+1), "다음"));
		
		pagingHTML.append("</ul>");
		pagingHTML.append("</nav>");
		return pagingHTML.toString();
	}
}













