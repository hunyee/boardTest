package kr.or.ddit.wrapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;

/**
 * Multipart request 의 경우, 비어있는 ParameterMap을 대신할 
 * 새로운 파라미터맵의 생성.
 * 업로드된 파일 데이터를 관리할 맵 생성.
 *
 */
public class FileUploadRequestWrapper extends HttpServletRequestWrapper {

	public FileUploadRequestWrapper(HttpServletRequest request) throws IOException {
		this(request, -1, null);
	}
	
	public FileUploadRequestWrapper(HttpServletRequest request, int sizeThreshold, File repository) throws IOException {
		super(request);
		encoding = request.getCharacterEncoding();
		parameterMap = new LinkedHashMap<String, String[]>();
		fileItemMap = new LinkedHashMap<String, List<FileItem>>();
		
		parameterMap.putAll(request.getParameterMap());
		
		parseRequest(request, sizeThreshold, repository);
		
	}

	private String encoding;
	private Map<String, String[]> parameterMap;
	private Map<String, List<FileItem>> fileItemMap;
	
	private void parseRequest(HttpServletRequest req, int sizeThreshold, File repository) throws IOException {
		DiskFileItemFactory itemFactory = new DiskFileItemFactory();
		if(sizeThreshold!=-1) {
			itemFactory.setSizeThreshold(sizeThreshold);
		}
		if(repository!=null) {
			itemFactory.setRepository(repository);
		}
		ServletFileUpload uploadHandler = new ServletFileUpload(itemFactory);
		try {
			List<FileItem> itemList = uploadHandler.parseRequest(req);
			if(itemList==null || itemList.size()==0) return;
			
			for(FileItem item : itemList) {
				String partName = item.getFieldName();
				if(item.isFormField()) {
					String paramValue = item.getString(encoding);
					String[] paramValues = parameterMap.get(partName);
					String[] newArray = null;
					if(paramValues==null) {
						newArray = new String[1];
					}else {
						newArray = new String[paramValues.length+1];
						System.arraycopy(paramValues, 0, newArray, 0, paramValues.length);
					}
					newArray[newArray.length-1] = paramValue;
					parameterMap.put(partName, newArray);
				}else {
					String filename = item.getName();
					if(StringUtils.isBlank(filename)) continue;
					List<FileItem> fileList = fileItemMap.get(partName);
					if(fileList==null) {
						fileList = new ArrayList<FileItem>();
						fileItemMap.put(partName, fileList);
					}
					fileList.add(item);
				} // if~else end
			} // for end
			
		} catch (FileUploadException e) {
			throw new IOException(e);
		} // try~catch end
	}
	
	@Override
	public String getParameter(String name) {
		if(parameterMap.containsKey(name)) {
			return parameterMap.get(name)[0];
		}else {
			return null;
		}
	}
	
	@Override
	public String[] getParameterValues(String name) {
		return parameterMap.get(name);
	}
	
	@Override
	public Map<String, String[]> getParameterMap() {
		return parameterMap;
	}
	
	@Override
	public Enumeration<String> getParameterNames() {
		final Iterator<String> paramNames = parameterMap.keySet().iterator();
		return new Enumeration<String>() {

			@Override
			public boolean hasMoreElements() {
				return paramNames.hasNext();
			}

			@Override
			public String nextElement() {
				return paramNames.next();
			}
		};
	}
	
	public FileItem getFileItem(String name){
		List<FileItem> fileList = fileItemMap.get(name);
		FileItem file = null;
		if(fileList!=null && fileList.size()>0) {
			file = fileList.get(0);
		}
		return file;
	}
	
	public List<FileItem> getFileItems(String name){
		return fileItemMap.get(name);
	}
	
	public Map<String, List<FileItem>> getFileItemMap() {
		return fileItemMap;
	}
	
	public Enumeration<String> getFileItemNames(){
		final Iterator<String> fileItemNames = fileItemMap.keySet().iterator();
		return new Enumeration<String>() {

			@Override
			public boolean hasMoreElements() {
				return fileItemNames.hasNext();
			}

			@Override
			public String nextElement() {
				return fileItemNames.next();
			}
		};
	}
	
	public void deleteTempFiles(){
		for(Entry<String, List<FileItem>> entry : fileItemMap.entrySet()) {
			List<FileItem> tempList = entry.getValue();
			for(FileItem temp : tempList) {
				temp.delete();
			}
		}
	}
}


















