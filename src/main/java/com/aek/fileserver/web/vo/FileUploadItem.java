package com.aek.fileserver.web.vo;

/**
 * 文件路径及文件名称
 *	
 * @author HongHui
 * @date   2017年11月6日
 */
public class FileUploadItem {
	
	private Integer status;
	private String url;
	private String msg;
	
	public FileUploadItem(Integer status,String url,String msg) {
		this.status = status;
		this.url = url;
		this.msg  = msg;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
	
}
