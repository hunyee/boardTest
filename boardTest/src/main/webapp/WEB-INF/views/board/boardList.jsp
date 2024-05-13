<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>   
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %> 

<script type="text/javascript" src="${pageContext.request.contextPath }/js/paging.js"></script>
<%-- <script type="text/javascript" src="${pageContext.request.contextPath }/includee/preScript.jsp"></script> --%>
<%-- <jsp:include page="${pageContext.request.contextPath }/includee/preScript.jsp"/> --%>
<script src="https://cdn.ckeditor.com/4.11.4/standard-all/ckeditor.js"></script>
<link rel="stylesheet" href="//code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
<script type="text/javascript" src="${pageContext.request.contextPath }/js/jquery-3.3.1.min.js"></script>
<script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js" ></script>


<link rel="canonical" href="https://getbootstrap.com/docs/4.3/examples/dashboard/">

<!-- Bootstrap core CSS -->
<link href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">

<style>
  .bd-placeholder-img {
    font-size: 1.125rem;
    text-anchor: middle;
    -webkit-user-select: none;
    -moz-user-select: none;
    -ms-user-select: none;
    user-select: none;
  }

  @media (min-width: 768px) {
    .bd-placeholder-img-lg {
      font-size: 3.5rem;
    }
  }
</style>
<!-- Custom styles for this template -->
<link href="<%=request.getContextPath() %>/css/dashboard.css" rel="stylesheet">

<%-- <a href="<c:url value='/board/boardInsert'/>">새글쓰기</a> --%>
<input type="button" class="btn btn-primary" value="새글쓰기" onclick="location.href='<c:url value="/board/boardInsert"/>';"/>
<table class="table table-bordered">
	<thead class="thead-dark">
		<tr>
			<th>번호</th>	
			<th>글번호</th>	
			<th>제목</th>	
			<th>작성자</th>	
			<th>작성일</th>	
			<th>조회수</th>	
		</tr>
	</thead>
	<tbody id="listBody">
		<c:set var="boardList" value="${pagingVO.dataList }"/>
		<c:forEach var="board" items="${boardList }">
			<tr>
<%-- 				<c:url value="/board/${board.bo_no}" var="boardViewURL"> --%>
<%-- 				<c:url value="/board/${board.bo_no}" var="boardViewURL"> --%>
<%-- 					<c:param name="what" value="${board.bo_no }"/> --%>
<%-- 				</c:url> --%>
				<td>${board.rnum }</td>
				<td>${board.bo_no }</td>
<%-- 				<td><a href="${boardViewURL }">${fn:replace(board.bo_title, "\\s", "&nbsp;") }</a></td> --%>
				<td><a href="${pageContext.request.contextPath }/board/${board.bo_no}">${fn:replace(board.bo_title, "\\s", "&nbsp;") }</a></td>
				
<%-- 				<a href="${pageContext.request.contextPath }/notice/${prev.board_no}"></a> --%>
				<td>${board.bo_writer }</td>
				<td>${board.bo_date }</td>
				<td>${board.bo_hit }</td>
			</tr>
		</c:forEach>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="6">
				<form name="searchHiddenForm">
					<input type="hidden" name="searchType" value="${pagingVO.searchType}"/>
					<input type="hidden" name="searchWord" value="${pagingVO.searchWord }"/>
					<input type="hidden" name="page"/>
				</form>
				<form name="searchForm" class="form-inline justify-content-center">
					<select name="searchType" class="form-control">
						<option value="" ${empty pagingVO.searchType ? "selected":"" }>전체</option>	
						<option value="title" ${pagingVO.searchType eq 'title' ? "selected":"" }>제목</option>	
						<option value="writer" ${pagingVO.searchType eq 'writer' ? "selected":"" }>작성자</option>	
						<option value="content" ${pagingVO.searchType eq 'content' ? "selected":"" }>내용</option>	
					</select>
					<input type="text" class="form-control ml-3 mr-3" name="searchWord" value="${pagingVO.searchWord }"/>
					<input type="submit" class="btn btn-success" value="검색" />
				</form>
				<p />
				<div id="pagingArea">
				${pagingVO.pagingHTML }
				</div>
			</td>
		</tr>
	</tfoot>
</table>
<script type="text/javascript">
// function paging(page){
// 	document.searchHiddenForm.page.value = page;
// 	document.searchHiddenForm.submit();
// }
makePaging({
	formTagName:"searchHiddenForm",
	functionName:"${pagingVO.functionName }",
	submitHandler:function(event){
		event.preventDefault();
		var queryString = $(event.target).serialize();
		console.log(queryString);
		event.target.page.value="";
		$.ajax({
			url : "${pageContext.request.contextPath }/board",
			data : queryString,
			dataType : "json", // request header(Accept), response header(Content-Type)
			success : function(resp) {
				var boardList = resp.dataList;
				var pagingHTML = resp.pagingHTML;
				var trTags = [];
				$(boardList).each(function(idx, board){
					var tr = $("<tr>")
									.append(
										$("<td>").text(board.rnum)		
										,$("<td>").text(board.bo_no)		
										,$("<td>").append(
													$("<a>").text(board.bo_title)
															.attr("href", "${pageContext.request.contextPath }/board/"+board.bo_no)
												 )
										,$("<td>").text(board.bo_writer)		 
										,$("<td>").text(board.bo_date)		 
										,$("<td>").text(board.bo_hit)		 
									);
					trTags.push(tr);
				});
				$("#listBody").html(trTags);
				$('#pagingArea').html(resp.pagingHTML);
			},
			error : function(errorResp) {
				console.log(errorResp.status);
			}
		});
		return false; // 동기 요청 취소
	}
});
</script>
