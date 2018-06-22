package com.aek.fileserver.web.config;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * 编码拦截器 
 * @author HongHui
 * @date   2017年11月10日
 */
public class CharacterEncodingInterceptor implements HandlerInterceptor{

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
			throws Exception {
		
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        request.setCharacterEncoding("UTF-8");  
		return true;
	}
	
	public static void main(String[] args) throws Exception {
		String filename = "QQ??20170705153714.jpg";
		//String str =URLDecoder.decode(URLDecoder.decode(filename));
		String str = new String(filename.getBytes("iso-8859-1"),"utf-8");
		System.out.println(str);
	}

	

}
