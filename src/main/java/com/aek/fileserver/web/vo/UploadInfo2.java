package com.aek.fileserver.web.vo;

import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class UploadInfo2 {

	@ApiModelProperty(required=true)
	private MultipartFile file;
	@ApiModelProperty(value="是否保留原文件名(默认:格式化;1:保留)", dataType="int", allowableValues=",0,1")
	private int holdOringName=0;//保留原文件名(默认:格式化,1:保留)

	public MultipartFile getFile() {
		return file;
	}
	public void setFile(MultipartFile file) {
		this.file = file;
	}
	public int getHoldOringName() {
		return holdOringName;
	}
	public void setHoldOringName(int holdOringName) {
		this.holdOringName = holdOringName;
	}
	
}
