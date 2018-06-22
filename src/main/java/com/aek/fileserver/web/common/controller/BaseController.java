/**
 */
package com.aek.fileserver.web.common.controller;

import java.nio.file.AccessDeniedException;
import java.text.ParseException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.aek.fileserver.exception.BusinessException;
import com.aek.fileserver.web.common.HttpCode;
import com.aek.fileserver.web.common.vo.Result;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * 控制器支持类
 * 
 * 
 */
public abstract class BaseController {
	protected Logger logger = LoggerFactory.getLogger(getClass());

	protected Result<Object> response(){
		return this.response(null);
	}

	protected <T> Result<T> response(Integer code, String msg){
		return new Result<T>(code, msg, null);
	}
	
	protected <T> Result<T> response(T data){
		return new Result<T>(HttpCode.OK.value(), HttpCode.OK.msg(), data);
	}

	/** 异常处理 */
	@ExceptionHandler(Exception.class)
	public void exceptionHandler(HttpServletRequest request, HttpServletResponse response, Exception ex)
			throws Exception {
		ModelMap modelMap = new ModelMap();
		HttpCode httpCode = null;
		if(ex instanceof BusinessException){
			logger.error("", ex);
			httpCode = HttpCode.INTERNAL_SERVER_ERROR;
			modelMap.put("code", httpCode.value());
			modelMap.put("msg", ex.getMessage());
		}else if(ex instanceof MethodArgumentNotValidException || ex instanceof ParseException){
			logger.error("", ex);
			httpCode = HttpCode.BAD_REQUEST;
			modelMap.put("code", httpCode.value());
			modelMap.put("msg", httpCode.msg());
		}else if(ex instanceof AccessDeniedException){
			httpCode = HttpCode.FORBIDDEN;
			modelMap.put("code", httpCode.value());
			modelMap.put("msg", httpCode.msg());
		}else{
			logger.error("error occured.", ex);
			httpCode = HttpCode.INTERNAL_SERVER_ERROR;
			modelMap.put("code", httpCode.value());
			modelMap.put("msg", httpCode.msg());
		}
		modelMap.put("path", request.getRequestURI());
		modelMap.put("contentType", request.getContentType());
		logger.debug(modelMap.toString());
		response.setContentType("application/json;charset=UTF-8");
		byte[] bytes = JSON.toJSONBytes(modelMap, SerializerFeature.DisableCircularReferenceDetect);
		response.getOutputStream().write(bytes);
	}
}
