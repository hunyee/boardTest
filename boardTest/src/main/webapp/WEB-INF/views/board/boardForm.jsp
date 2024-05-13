<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>	
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

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

<c:if test="${empty board.bo_no or board.bo_no eq 0 }">
	<c:url value="/board" var="action" />
	<c:url value="post" var="method" />
</c:if>
<c:if test="${not empty board.bo_no and board.bo_no gt 0 }">
	<c:url value="/board/${board.bo_no }" var="action" />
	<c:url value="put" var="method" />
</c:if>

<form:form modelAttribute="board" id="boardForm" action="${action }" method="${method}" class="form-inline" enctype="multipart/form-data">
	<input type="hidden" name="bo_no" value="${board.bo_no }" />
	<input type="hidden" name="code_id"  value="BT01" />
	<input type="hidden" name="bo_ip"  value="${pageContext.request.remoteAddr }" />
	<input type="hidden" name="bo_parent" value="${param.parent }" />
		<table class="table table-bordered">
			<tr>
				<th>제목</th>
				<td><input class="form-control" type="text" name="bo_title" 
					value="${board.bo_title }" 
					placeholder="${not empty param.parent?'RE:':'' }"
					/><form:errors path="bo_title" element="span" cssClass="error" /></td>
			</tr>
			<tr>
				<th>작성자</th>
				<td><input class="form-control" type="text" name="bo_writer" 
					value="${board.bo_writer }" /><form:errors path="bo_writer" element="span" cssClass="error" /></td>
			</tr>
			<tr>
				<th>비밀번호</th>
				<td><input class="form-control" type="text" name="bo_pass"  />
				<form:errors path="bo_pass" element="span" cssClass="error" /></td>
			</tr>
			<tr>
				<th>이메일</th>
				<td><input class="form-control" type="text" name="bo_mail" value="${board.bo_mail }" /><span
					class="error">${errors["bo_mail"]}</span></td>
			</tr>
			<c:if test="${not empty board.savedPdsList }">
			<tr>
				<th>기존 첨부파일</th>
				<td>
					<c:forEach items="${board.savedPdsList }" var="pds" varStatus="vs">
						<c:if test="${not empty pds.pds_filename }">
							<span>
								${pds.pds_filename }
								<input type="image" style="width: 20px; height: 20px;" 
									src="<c:url value='/images/delete.png'/>"
									id="del_${pds.pds_no }"
									class="deleteBtn" 
								/>
								&nbsp;&nbsp;
								${not vs.last?"|":"" }
								&nbsp;&nbsp;
							</span>
						</c:if>
					</c:forEach>
				</td>
			</tr>
			</c:if>
			<tr>
				<th>첨부파일</th>
				<td>
					<input type="file" name="bo_files" />
					<input type="file" name="bo_files" />
					<input type="file" name="bo_files" />
				</td>
			</tr>
			<tr>
				<th>내용</th>
				<td><textarea name="bo_content" id="bo_content"  rows="5" cols="70">${board.bo_content }</textarea>
					<form:errors path="bo_content" element="span" cssClass="error" /></td>
			</tr>
			<tr>
				<td colspan="2">
					<input type="submit" class="btn btn-primary" value="전송" />
					<input type="reset" class="btn btn-primary" value="취소" />
					<input type="button" class="btn btn-primary" value="뒤로가기" 
						onclick="history.back();"
					/>
					<input type="button" class="btn btn-primary" value="목록으로" 
						onclick="location.href='<c:url value="/board"/>';"
					/>
				</td>
			</tr>
		</table>
	</form:form>
	<script type="text/javascript">
		var boardForm = $("#boardForm");
		$(".deleteBtn").on("click", function(){
			var regex = /del_([0-9]*)/g;
			var btnId = $(this).prop("id");
			var pdsNo = regex.exec(btnId)[1];
			boardForm.append(
				$("<input>").attr({
					type:"text"
					, name:"deletePdsNos"
					, value:pdsNo
				})		
			);
			$(this).parent("span").remove();
		});
		
		CKEDITOR.replace("bo_content", {
// 			extraPlugins: 'uploadImage',
			filebrowserImageUploadUrl:"<c:url value='/board/imageUpload.do'/>?sample=test"
		});
		<c:if test="${not empty message}">
			alert("${message}");
			<c:remove var="message" scope="session"/>
		</c:if>
	</script>












