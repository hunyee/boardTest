<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>	

<script type="text/javascript" src="${pageContext.request.contextPath }/js/paging.js"></script>

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

<c:if test="${not empty board.bo_no and board.bo_no gt 0 }">
	<c:url value="delete" var="method" />
</c:if>

<script type="text/javascript">
	<c:if test="${not empty message}">
		alert("${message}");
	</c:if>
	$(function(){
		function dataListModify(resp){
			var replyList = resp.dataList;
			var pagingHTML = resp.pagingHTML;
			var trTags = [];
			$(replyList).each(function(idx, reply){
				var tr1 = $("<tr>").append(
							$("<td>").text(reply.rep_writer)				
							,$("<td>").text(reply.rep_ip)				
							,$("<td>").text(reply.rep_date)				
						 );
				var tr2 = $("<tr>").append(
							$("<td>").attr("colspan", "2")
									 .text(reply.rep_content)
							,$("<td>").append(
										$("<input>").attr({
											type:"button"
											, 'class':"replyDelBtn btn btn-info"
											, value:"삭제"
										})
										, $("<input>").attr({
											type:"text"
											, name:"rep_pass"
											, id:"del_"+reply.rep_no
											, class:"replyDelPass"
											, style:"display:none;"
										})		
									)		 
						 );
				trTags.push(tr1);
				trTags.push(tr2);
			});
			$("#listBody").html(trTags);
			$('#pagingArea').html(resp.pagingHTML);
		}
		
		makePaging({
			formTagName:"hiddenForm",
			functionName:"paging",
			submitHandler:function(event){
				event.preventDefault();
				var queryString = $(event.target).serialize();
				event.target.page.value="";
				$.ajax({
					url : "${pageContext.request.contextPath }/reply/replyList.do",
					data : queryString,
					dataType : "json", // request header(Accept), response header(Content-Type)
					success : function(resp) {
						dataListModify(resp);
					},
					error : function(errorResp) {
						console.log(errorResp.status);
					}
				});
				return false; // 동기 요청 취소
			}
		});
		$("#replyTable").hide();
		var replyTable = $("#replyTable");
		$("#replyViewBtn").on("click", function(){
			if(!replyTable.is(":visible")){
				$(this).val("댓글 숨기기");
				paging(1);
			}else{
				$(this).val("댓글 보기");
			}
			replyTable.toggle();
		});
		$("#replyForm").on("submit", function(event){
			event.preventDefault();
			var form = $(this);
			var queryString = $(this).serialize();
			var action = $(this).attr("action");
			$.ajax({
				url : action,
				method : "post",
				data : queryString,
				dataType : "json", // request header(Accept), response header(Content-Type)
				success : function(resp) {
					if(resp.dataList){
						dataListModify(resp);
						form[0].reset();
						replyTable.show();
					}else{
						if(resp.message){
							alert(resp.message);
						}
					}
				},
				error : function(errorResp) {
					console.log(errorResp.status);
				}
			});
			return false;
		});
		
		$("#listBody").on("click", ".replyDelBtn", function(event){
			var replyDelPass = $(this).siblings(".replyDelPass");
			replyDelPass.show();
			replyDelPass.focus();
			$(this).hide();
		});
		$("#listBody").on("keypress", ".replyDelPass", function(event){
// 			console.log(event.keyCode);
			var replyDelPass = $(this);
			var repPass = $(this).val();
			var repNo = $(this).prop("id").substr(4);
			var boNo = ${board.bo_no};
			if(event.keyCode == 13){
				$.ajax({
					url : "<c:url value='/reply/replyDelete.do'/>",
					method : "post",
					data : {
						rep_no : repNo
						, bo_no : boNo
						, rep_pass : repPass
					},
					dataType : "json", // request header(Accept), response header(Content-Type)
					success : function(resp) {
						if(resp.dataList){
							dataListModify(resp);
							replyTable.show();
						}else{
							if(resp.message){
								alert(resp.message);
								$(".replyDelPass").val("");
								$(".replyDelPass").hide();
								replyDelPass.siblings(".replyDelBtn").show();								
							}
						}
					},
					error : function(errorResp) {
						console.log(errorResp.status);
					}
				});
			}
		});
		
		$("#singoBtn").on("click", function(){
			var singo = $(this);
			$.ajax({
				url : "<c:url value='/board/boardReport.do'/>",
				data : {
					what:${board.bo_no}
				},
				dataType : "text", // request header(Accept), response header(Content-Type)
				success : function(resp) {
					if(resp=="SUCCESS"){
						var singoCnt = singo.siblings("span:first").text();
						singoCnt = parseInt(singoCnt)+1;
						console.log(singoCnt);
						singo.siblings("span:first").text(singoCnt);
						singo.remove();
					}else{
						
					}
				},
				error : function(errorResp) {
					console.log(errorResp.status);
				}
			});
		});
		
		$(".otherBtn").on("click", function(){
			var btnKind = $(this).prop("id");
			var url = "<c:url value='/board/like.do'/>";
			if(btnKind=="SINGO"){
				url = "<c:url value='/board/boardReport.do'/>"
			}
			var button = $(this);
			var comment = $(this).data("comment");
			$.ajax({
				url : url,
				data : {
					what : ${board.bo_no}
					, type : btnKind 
				},
				dataType : "text", // request header(Accept:text/plain), response header(Content-Type)
				success : function(resp) {
					if(resp=="SUCCESS"){
						var countTag = button.siblings("span."+btnKind);
						countTag.text(parseInt(countTag.text())+1);
						button.remove();
					}else{
						alert(comment+" 실패");
					}
				},
				error : function(errorResp) {
					console.log(errorResp.status);
				}
			});
		});
		$("#deleteFormModal").on("hidden.bs.modal", function(){
			document.eachForm.reset();
		});
	});
	function boardUpdate(bo_pass){
		$("#deleteFormModalTitle").text("글수정 폼");
		$("#formSubmit").text("수정");
		$("[name='eachForm']").attr({
			action:"<c:url value='/board/boardUpdateForm'/>"
		});
		$('#deleteFormModal').modal();
	}
	function boardDelete(){
		$("#deleteFormModalTitle").text("글삭제 폼");
		$("#formSubmit").text("삭제");
		$("[name='eachForm']").attr({
			action:"<c:url value='/board/${board.bo_no}'/>"
		});
		$('#deleteFormModal').modal();
	}
</script>
	<table class="table table-bordered">
		<tr>
			<th>게시판분류</th>
			<td>${board.code_name }</td>
		</tr>
		<tr>
			<th>글번호</th>
			<td>${board.bo_no }</td>
		</tr>
		<tr>
			<th>제목</th>
			<td>${board.bo_title }</td>
		</tr>
		<tr>
			<th>작성자</th>
			<td>${board.bo_writer }</td>
		</tr>
		<tr>
			<th>작성일</th>
			<td>${board.bo_date }</td>
		</tr>
		<tr>
			<th>조회수</th>
			<td>${board.bo_hit }</td>
		</tr>
		<tr>
			<th>뭔가 더 해볼람?</th>	
			<td>
				신고수 : 
				<span class="SINGO">
					${board.bo_report }
				</span>
				<c:if test="${not fn:contains(cookie.singoCookie.value, board.bo_no) }">
					<input type="button" class="btn btn-info otherBtn"
							 id="SINGO" value="신고" data-comment="신고"/>
				</c:if>		 
				좋아요 : 
				<span class="LIKE">
					${board.bo_like }
				</span>
				<c:if test="${not fn:contains(cookie.likeCookie.value, board.bo_no) }">
					<input type="button" class="btn btn-info otherBtn" 
							id="LIKE" value="조아"  data-comment="추천"/>
				</c:if>		
				싫어요 : 
				<span class="HATE">
					${board.bo_hate }
				</span>
				<c:if test="${not fn:contains(cookie.likeCookie.value, board.bo_no) }">
					<input type="button" class="btn btn-info otherBtn" 
							id="HATE" value="시르"  data-comment="비추천"/>
				</c:if>		
			</td>
		</tr>
		<tr>
			<th>아이피</th>
			<td>${board.bo_ip }</td>
		</tr>
		<tr>
			<th>이메일</th>
			<td>${board.bo_mail }</td>
		</tr>
		<tr>
			<th>부모글번호</th>
			<td>${board.bo_parent }</td>
		</tr>
		<tr>
			<th>첨부파일</th>
			<td>
				<c:forEach items="${board.savedPdsList }" var="pds" varStatus="vs">
				<c:if test="${not empty pds.pds_filename }">
					<c:url value="/board/download.do" var="downloadURL">
						<c:param name="what" value="${pds.pds_no }" />
					</c:url>				
					<a href="${downloadURL }" title="파일크기:${pds.pds_fancysize }">${pds.pds_filename }</a>
					&nbsp;&nbsp;
					${not vs.last?"|":"" }
					&nbsp;&nbsp;
				</c:if>	
				</c:forEach>
			</td>
		</tr>
		<tr>
			<th>내용</th>
			<td>${board.bo_content }</td>
		</tr>
		<tr>
			<td colspan="2">
				<input type="button" value="뒤로가기" class="btn btn-info"
					onclick="history.back();"
				/>
				<input type="button" value="목록으로" class="btn btn-info"
					onclick="location.href='<c:url value="/board"/>';"
				/>
				<c:url value="/board/boardInsert" var="boardInsertURL">
					<c:param name="parent" value="${board.bo_no }" />
				</c:url>
				<input type="button" value="답글쓰기" class="btn btn-info"
					onclick="location.href='${boardInsertURL}';"
				/>
				<c:url value="/board/boardUpdate" var="boardUpdateUrl">
					<c:param name="what" value="${board.bo_no }" />
				</c:url>
				<input type="button" value="수정" class="btn btn-info"
					onclick="boardUpdate();"
				/>
				<input type="button" value="삭제" class="btn btn-info"
					onclick="boardDelete();"
				/>
			</td>
		</tr>
	</table>
<form name="hiddenForm">
	<input type="hidden" name="bo_no" value="${board.bo_no }" />
	<input type="hidden" name="page" />
</form>
<form id="replyForm" action="<c:url value='/reply/replyInsert.do'/>"
	class="form-inline"
>
	<input type="hidden" name="bo_no" value="${board.bo_no }"/>
	<input type="hidden" name="rep_ip" value="${pageContext.request.remoteAddr }"/>
	<table>
	<tr>
		<td>작성자 : <input class="form-control" type="text" name="rep_writer" /></td>
		<td>비번 : <input class="form-control" type="text" name="rep_pass" /></td>
	</tr>
	<tr>
		<td colspan="2">
			<textarea name="rep_content" class="form-control" cols="50" rows="4"></textarea>
			<input type="submit" value="댓글 작성" class="btn btn-info"/>
		</td>
	</table>
</form>
<input id="replyViewBtn" type="button" class="btn btn-info" value="댓글보기 " />
<table class="table table-bordered" id="replyTable">
	<thead>
	<tr>
		<th>댓글작성자</th>
		<th>아이피</th>
		<th>작성일</th>
	</tr>
	<tr>
		<th colspan="3">
			댓글 내용
		</th>
	</tr>
	</thead>
	<tbody id="listBody">
	
	</tbody>
	<tfoot id="pagingArea">
	
	</tfoot>
</table>	
<div class="modal fade" id="deleteFormModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalCenterTitle" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="deleteFormModalTitle">글삭제폼</h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <form name="eachForm" method="post" action="<c:url value='/board/${board.bo_no }'/>">
	      <div class="modal-body">
	        	<input type="hidden" name="bo_no" value="${board.bo_no }"/>
	        	비밀 번호 : <input type="text" name="bo_pass" />
	      </div>
	      <div class="modal-footer">
	        <button type="submit" id="formSubmit" class="btn btn-primary">삭제</button>
	        <button type="button" class="btn btn-secondary" data-dismiss="modal">닫기</button>
	      </div>
      </form>
    </div>
  </div>
</div>
