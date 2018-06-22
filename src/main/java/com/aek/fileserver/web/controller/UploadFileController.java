package com.aek.fileserver.web.controller;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.aek.fileserver.web.common.HttpCode;
import com.aek.fileserver.web.common.controller.BaseController;
import com.aek.fileserver.web.common.vo.Result;
import com.aek.fileserver.web.vo.FileItem;
import com.aek.fileserver.web.vo.FileUploadItem;
import com.aek.fileserver.web.vo.UploadInfo;
import com.aek.fileserver.web.vo.UploadInfo2;
import com.aek.fileserver.web.vo.UploadSingleFileInfo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;

@RestController
@RequestMapping
@Api(description="文件上传API")
public class UploadFileController extends BaseController {

	//设备平台文件上传路径
	@Value("${file.path}")
	private String filePath;
	//装备中心文件上传路径
	@Value("${file.zbzx.path}")
	private String zbzxFilePath;
	//文件存放实际路径
	@Value("${file.base.path}")
	private String baseFilePath;
	//文件访问域名前缀
	@Value("${file.domain}")
	private String domain;

	//private DateFormat DF = new SimpleDateFormat("yyyy/MM/dd");
	private DateFormat DF = new SimpleDateFormat("yyyy/MM/dd/HHmmssS");

	@PostMapping("/upload")
	@ApiOperation(value = "上传文件")
	@ApiResponse(code = 200, message = "OK")
	public Result<List<String>> upload(@ModelAttribute UploadInfo info,HttpServletRequest request,HttpServletResponse response) throws IOException{
		logger.info("1=request encode = " + request.getCharacterEncoding());
		request.setCharacterEncoding("UTF-8");  
	    response.setCharacterEncoding("UTF-8");  
	    logger.info("2=request encode = " + request.getCharacterEncoding());
		MultipartFile[] files = info.getFiles();
		if(files==null||files.length==0){
			return response(HttpCode.BAD_REQUEST.value(), "上传文件不能为空.");
		}
		List<String> retVal = doUploadFile(info);
		return response(retVal);
	}
	
	@PostMapping("/upload2")
	@ApiOperation(value = "上传文件,保留原文件名,在“年/月/日/时分秒毫秒+四位随机数”目录下同名则覆盖")
	@ApiResponse(code = 200, message = "OK")
	public Result<List<String>> upload2(@ModelAttribute UploadInfo info,HttpServletRequest request,HttpServletResponse response) throws IOException{
		logger.info("1=request encode = " + request.getCharacterEncoding());
		request.setCharacterEncoding("UTF-8");  
	    response.setCharacterEncoding("UTF-8");  
	    logger.info("2=request encode = " + request.getCharacterEncoding());
		MultipartFile[] files = info.getFiles();
		if(files==null||files.length==0){
			return response(HttpCode.BAD_REQUEST.value(), "上传文件不能为空.");
		}
		info.setHoldOringName(1);
		List<String> retVal = doUploadFile(info);
		return response(retVal);
	}
	
	@PostMapping("/upload3")
	@ApiOperation(value = "上传文件,保留原文件名,在“年/月/日/时分秒毫秒+四位随机数”目录下同名则覆盖，返回源文件名称及上传后路径")
	@ApiResponse(code = 200, message = "OK")
	public Result<List<FileItem>> upload3(@ModelAttribute UploadInfo info,HttpServletRequest request,HttpServletResponse response) throws IOException{
		logger.info("1=request encode = " + request.getCharacterEncoding());
		request.setCharacterEncoding("UTF-8");  
	    response.setCharacterEncoding("UTF-8");  
	    logger.info("2=request encode = " + request.getCharacterEncoding());
		MultipartFile[] files = info.getFiles();
		if(files==null||files.length==0){
			return response(HttpCode.BAD_REQUEST.value(), "上传文件不能为空.");
		}
		info.setHoldOringName(1);
		List<FileItem> retVal = doUploadOriginalFile(info);
		return response(retVal);
	}
	
	@PostMapping("/upload/zbzx")
	@ApiOperation(value = "装备中心上传文件,保留原文件名,在“年/月/日/时分秒毫秒+四位随机数”目录下同名则覆盖，返回源文件名称及上传后路径")
	@ApiResponse(code = 200, message = "OK")
	public Result<List<FileItem>> zbzxUpload(@ModelAttribute UploadInfo info,HttpServletRequest request,HttpServletResponse response) throws IOException{
		logger.info("1=request encode = " + request.getCharacterEncoding());
		request.setCharacterEncoding("UTF-8");  
	    response.setCharacterEncoding("UTF-8");  
	    logger.info("2=request encode = " + request.getCharacterEncoding());
		MultipartFile[] files = info.getFiles();
		if(files==null||files.length==0){
			return response(HttpCode.BAD_REQUEST.value(), "上传文件不能为空.");
		}
		info.setHoldOringName(1);
		List<FileItem> retVal = doZbzxUploadFile(info);
		return response(retVal);
	}
	
	@PostMapping("/upload/zbzx4weixin")
	@ApiOperation(value = "装备中心上传文件,保留原文件名,在“年/月/日/时分秒毫秒+四位随机数”目录下同名则覆盖，返回源文件名称及上传后路径")
	@ApiResponse(code = 200, message = "OK")
	public Result<List<FileItem>> zbzxUpload4Weixin(HttpServletRequest request) throws IOException{
		List<MultipartFile> files = new ArrayList<MultipartFile>();  
        try {  
            CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(  
                    request.getSession().getServletContext());  
            if (request instanceof MultipartHttpServletRequest) {  
                // 将request变成多部分request  
                MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;  
                Iterator<String> iter = multiRequest.getFileNames();  
                // 检查form中是否有enctype="multipart/form-data"  
                if (multipartResolver.isMultipart(request) && iter.hasNext()) {  
                    // 获取multiRequest 中所有的文件名  
                    while (iter.hasNext()) {  
                        // 一次遍历所有文件  
                        // MultipartFile file =  
                        // multiRequest.getFile(iter.next().toString());  
                        // if (file != null) {  
                        // files.add(file);  
                        // }  
                        // 适配名字重复的文件  
                        List<MultipartFile> fileRows = multiRequest  
                                .getFiles(iter.next().toString());  
                        if (fileRows != null && fileRows.size() != 0) {  
                            for (MultipartFile file : fileRows) {  
                                if (file != null && !file.isEmpty()) {  
                                    files.add(file);  
                                }  
                            }  
                        }  
                    }  
                }  
            }  
        } catch (Exception ex) {  
        	ex.printStackTrace();
        }  
		if(files.size()==0){
			return response(HttpCode.BAD_REQUEST.value(), "上传文件不能为空.");
		}
		UploadInfo info = new UploadInfo();
		MultipartFile[] array = new MultipartFile[files.size()];
		MultipartFile[] s=files.toArray(array);
		info.setFiles(s);
		info.setHoldOringName(1);
		List<FileItem> retVal = doZbzxUploadFile(info);
		return response(retVal);
	}
	
	/**
	 * 装备中心单个文件上传
	 * @param info
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@PostMapping("/upload/zbzxfile")
	@ApiOperation(value = "装备中心上传单个文件,保留原文件名,在“年/月/日/时分秒毫秒+四位随机数”目录下同名则覆盖，返回源文件名称及上传后路径")
	@ApiResponse(code = 200, message = "OK")
	public FileUploadItem zbzxUploadSingleFile(@ModelAttribute UploadSingleFileInfo info,HttpServletRequest request,HttpServletResponse response) throws IOException{
		logger.info("1=request encode = " + request.getCharacterEncoding());
		request.setCharacterEncoding("UTF-8");  
	    response.setCharacterEncoding("UTF-8");  
	    logger.info("2=request encode = " + request.getCharacterEncoding());
		MultipartFile file = info.getFile();
		if(file==null){
			return new FileUploadItem(HttpCode.BAD_REQUEST.value(),"","上传文件不能为空");
		}
		info.setHoldOringName(1);
		FileUploadItem retVal = doZbzxUploadFile(info);
		return retVal;
	}
	
	/**
	 * 设备平台下载文件
	 * @param path
	 * @param request
	 * @param response
	 * @return
	 */
	@GetMapping("/download")
	@ApiImplicitParams({ @ApiImplicitParam(name = "path", value = "文件路径", paramType = "query", required = true) })
	@ApiOperation(value = "设备平台下载文件")
	public Result<Object> downloadFile(String path,HttpServletRequest request,HttpServletResponse response){
		try {
			logger.info("下载文件路径="+path);
			path = URLDecoder.decode(path, "UTF-8");
			logger.info("解码下载文件路径="+path);
			String filepath = this.baseFilePath+this.filePath + path;
			filepath = filepath.replace("/", File.separator); 
			File file = new File(filepath);
			response.setContentType("multipart/form-data");
			String fileName = file.getName();
			String header = request.getHeader("User-Agent").toUpperCase();
			if (header.contains("MSIE") || header.contains("TRIDENT") || header.contains("EDGE")) {
				fileName = URLEncoder.encode(fileName, "UTF-8");
				//IE下载文件名空格变+号问题
				fileName = fileName.replace("+", "%20");
			} else {
			    fileName = new String(fileName.getBytes(), "ISO8859-1");
			}
			response.setHeader("Content-disposition", "attachment; filename=\"" + fileName + "\"");
			InputStream in = new FileInputStream(filepath);  
	        OutputStream out = response.getOutputStream();  
	        int b;  
	        while((b=in.read())!= -1){  
	            out.write(b);  
	        }  
	        in.close();  
	        out.close();  
		} catch (Exception e) {
			e.printStackTrace();
			return response(HttpCode.BAD_REQUEST.value(), "文件服务器异常，请稍后再试");
		}
		return response();
	}

	/**
	 * 设备平台上传文件
	 * @param info
	 * @return
	 * @throws IOException
	 */
	private List<String> doUploadFile(UploadInfo info) throws IOException {
		List<String> retVal = new ArrayList<String>();
		if(!filePath.endsWith("/")){
			filePath = filePath+"/";
		}
		//文件保存路径：年/月/日/时分秒毫秒+四位随机数
		String day = DF.format(new Date())+(int)((Math.random()*9+1)*1000);
		String dir = filePath+day+"/";
		for (MultipartFile file : info.getFiles()) {
			String oriFileName = file.getOriginalFilename();
			String fileName = oriFileName;  
			if(1!=info.getHoldOringName()){
				String suffix = "";
				int suffixIndex = oriFileName.lastIndexOf(".");
				if (suffixIndex!=-1) {
					suffix = oriFileName.substring(suffixIndex);
				}
				fileName = UUID.randomUUID().toString().replaceAll("-", "")+suffix;
			}
			File outDir = new File(dir);
			if(!outDir.exists()){
				outDir.mkdirs();
			}
			File outFile = new File(dir+fileName);
			if(logger.isDebugEnabled()){
				logger.debug(oriFileName+":"+outFile.getAbsolutePath());
			}
			retVal.add("/"+day+"/"+fileName);
			FileCopyUtils.copy(file.getBytes(), outFile);
		}
		return retVal;
	}
	
	/**
	 * 设备平台上传文件保留源文件名称
	 * @param info
	 * @return
	 * @throws IOException
	 */
	private List<FileItem> doUploadOriginalFile(UploadInfo info) throws IOException {
		List<FileItem> fileItems = new ArrayList<FileItem>();
		if(!filePath.endsWith("/")){
			filePath = filePath+"/";
		}
		//文件保存路径：年/月/日/时分秒毫秒+四位随机数
		String day = DF.format(new Date())+(int)((Math.random()*9+1)*1000);
		String dir = filePath+day+"/";
		for (MultipartFile file : info.getFiles()) {
			String oriFileName = file.getOriginalFilename();
			String fileName = oriFileName;  
			logger.info("文件名称="+fileName);
			if(1!=info.getHoldOringName()){
				String suffix = "";
				int suffixIndex = oriFileName.lastIndexOf(".");
				if (suffixIndex!=-1) {
					suffix = oriFileName.substring(suffixIndex);
				}
				fileName = UUID.randomUUID().toString().replaceAll("-", "")+suffix;
			}
			File outDir = new File(dir);
			if(!outDir.exists()){
				outDir.mkdirs();
			}
			File outFile = new File(dir+fileName);
			if(logger.isDebugEnabled()){
				logger.debug(oriFileName+":"+outFile.getAbsolutePath());
			}
			FileItem item = new FileItem(fileName,"/"+day+"/"+fileName);
			fileItems.add(item);
			FileCopyUtils.copy(file.getBytes(), outFile);
		}
		return fileItems;
	}
	
	/**
	 * 装备中心文件上传
	 * @param info
	 * @return
	 * @throws IOException
	 */
	private List<FileItem> doZbzxUploadFile(UploadInfo info) throws IOException {
 		List<FileItem> fileItems = new ArrayList<FileItem>();
		if(!filePath.endsWith("/")){
			filePath = filePath+"/";
		}
		if(!zbzxFilePath.endsWith("/")){
			zbzxFilePath = zbzxFilePath+"/";
		}
		String uploadFilePath = filePath + zbzxFilePath;
		//文件保存路径：年/月/日/时分秒毫秒+四位随机数
		String day = DF.format(new Date())+(int)((Math.random()*9+1)*1000);
		String dir = uploadFilePath+day+"/";
		for (MultipartFile file : info.getFiles()) {
			String oriFileName = file.getOriginalFilename();
			String fileName = oriFileName;  
			logger.info("文件名称="+fileName);
			if(1!=info.getHoldOringName()){
				String suffix = "";
				int suffixIndex = oriFileName.lastIndexOf(".");
				if (suffixIndex!=-1) {
					suffix = oriFileName.substring(suffixIndex);
				}
				fileName = UUID.randomUUID().toString().replaceAll("-", "")+suffix;
			}
			File outDir = new File(dir);
			if(!outDir.exists()){
				outDir.mkdirs();
			}
			File outFile = new File(dir+fileName);
			if(logger.isDebugEnabled()){
				logger.debug(oriFileName+":"+outFile.getAbsolutePath());
			}
			FileItem item = new FileItem(fileName,"/"+zbzxFilePath + day + "/" + fileName);
			fileItems.add(item);
			FileCopyUtils.copy(file.getBytes(), outFile);
		}
		return fileItems;
	}
	
	/**
	 * 装备中心单个文件上传,返回全路径,兼容富文本文件上传
	 * @param info
	 * @return
	 * @throws IOException
	 */
	private FileUploadItem doZbzxUploadFile(UploadSingleFileInfo info) throws IOException {
		if(!filePath.endsWith("/")){
			filePath = filePath+"/";
		}
		if(!zbzxFilePath.endsWith("/")){
			zbzxFilePath = zbzxFilePath+"/";
		}
		String uploadFilePath = filePath + zbzxFilePath;
		//文件保存路径：年/月/日/时分秒毫秒+四位随机数
		String day = DF.format(new Date())+(int)((Math.random()*9+1)*1000);
		String dir = uploadFilePath+day+"/";
		MultipartFile file = info.getFile();
		String oriFileName = file.getOriginalFilename();
		String fileName = oriFileName;  
		logger.info("文件名称="+fileName);
		if(1!=info.getHoldOringName()){
			String suffix = "";
			int suffixIndex = oriFileName.lastIndexOf(".");
			if (suffixIndex!=-1) {
				suffix = oriFileName.substring(suffixIndex);
			}
			fileName = UUID.randomUUID().toString().replaceAll("-", "")+suffix;
		}
		File outDir = new File(dir);
		if(!outDir.exists()){
			outDir.mkdirs();
		}
		File outFile = new File(dir+fileName);
		if(logger.isDebugEnabled()){
			logger.debug(oriFileName+":"+outFile.getAbsolutePath());
		}
		FileUploadItem item = new FileUploadItem(1,domain + "/"+zbzxFilePath + day + "/" + fileName,"上传成功");
		FileCopyUtils.copy(file.getBytes(), outFile);
		return item;
	}

	
}
