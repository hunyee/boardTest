/**
 * @param pagingInfo : functionName(페이징 함수명, 필수), 
 *					   formTagName(검색폼의 name값, 필수),  
 *					   pageTagName(currentPage 파라미터명, 옵션),
 *						submitHandler(onsubmit 에 사용할 이벤트 핸들러, 옵션)
 * @returns
 */
function makePaging(pagingInfo){
	var formTag = document[pagingInfo.formTagName];
	if(!formTag){
		var newForm = document.createElement("form");
		newForm.name = pagingInfo.formTagName;
		document.body.appendChild(newForm);
	}
	if(pagingInfo.submitHandler){
		document[pagingInfo.formTagName].onsubmit=pagingInfo.submitHandler;
	}
	window[pagingInfo.functionName] = function (page){
						var defaultName = "page";
						if(pagingInfo.pageTagName){
							defaultName = pageTagName;
						}
						var formTag = document[pagingInfo.formTagName];
						if(!formTag[defaultName]){
							var newInput = document.createElement("input");
							newInput.name=defaultName;
							newInput.type="hidden";
							formTag.appendChild(newInput);
						}
						formTag[defaultName].value=page;
						var submitBtn = formTag.querySelector("[type='submit']");
						if(submitBtn){
							submitBtn.click();
						}else{
							var event = new Event("submit", {cancelable:true});
							var canceled = !formTag.dispatchEvent(event);
							if(!canceled){
								formTag.submit();
							}
						}
						return false;
					}
}
	











