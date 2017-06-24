package com.jt.manage.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.RandomUtils;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.jt.common.spring.exetend.PropertyConfig;
import com.jt.common.vo.PicUploadResult;
import com.jt.manage.service.PropertyService;

@Controller
public class PicUploadController {
	private static final Logger log = Logger.getLogger(PicUploadController.class);
	
	@PropertyConfig
	public String REPOSITORY_PATH;	//不能在controller中注入属性
	
	@Autowired
	private PropertyService propertyService;
	
	//文件上传	/pic/upload 注意：uploadFile是common.js中25行定义名称，就等同于自己写的文件上传框的名称
	@RequestMapping("/pic/upload")
	@ResponseBody
	public PicUploadResult picUpload(MultipartFile uploadFile){
		/*
		 * 文件上传步骤：
		 * 1）拿到这个文件文件名，扩展名，判断是否合法的图片文件后缀
		 * 2）判断它是否为木马
		 * 3）生成两个路径：图片存放路径，图片网络上访问的相当路径
		 * 4）图片存放目录，images文件太多，不方便管理，yyyy/MM/dd
		 * 5）图片文件名重新命名currentTime+3位随机数/uuid+3位随机数
		 * 6）图片保存
		 * 7）返回这个对象的PicUploadResult，提前设置error/url/height/width
		 */
		
		PicUploadResult result = new PicUploadResult();
		
		String oldFileName = uploadFile.getOriginalFilename();
		String extFileName = oldFileName.substring(oldFileName.lastIndexOf("."));
		
		//文件后缀判断 .jpg/.gif/.png
		if(!extFileName.matches("^.*(jpg|gif|png)$")){
			result.setError(1);
		}else{
			//如果它是一个木马文件，它就不能被转成图片对象，它就没有height，width
			try{
				BufferedImage bufImage = ImageIO.read(uploadFile.getInputStream());
				result.setHeight(bufImage.getHeight()+"");
				result.setWidth(bufImage.getWidth()+"");
				
				String dir = new SimpleDateFormat("yyyy/MM/dd").format(new Date())+"/";
				//绝对路径：C:\jt-upload\images\2016\12\06\
				String path = propertyService.REPOSITORY_PATH+"/images/" + dir;
				//创建目录：
				File _dir = new File(path);
				if(!_dir.exists()){		//如果目录不存在就创建
					_dir.mkdirs();		//创建多级目录
				}
				
				String urlPrefix = propertyService.IMAGE_BASE_URL+"/images/"+dir;	//相当路径前缀
				String fileName = System.currentTimeMillis() +""+ RandomUtils.nextInt(100, 999)+extFileName;
				
				result.setUrl(urlPrefix+fileName);
				
				uploadFile.transferTo(new File(path+fileName));		//文件保存到磁盘
			}catch(Exception e){
				log.error(e.getMessage());
				result.setError(1);		//图片转换时报错
			}
		}
		return result;
	}
}
