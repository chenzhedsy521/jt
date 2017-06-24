package com.jt.jsoup.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jt.jsoup.pojo.Item;

public class JDUtil {
	private static final Logger log = Logger.getLogger(JDUtil.class);
	private static final ObjectMapper MAPPER = new ObjectMapper();
	
	/*
	 * 步骤：
	 * 1、获取所有的三级分类链接，共计1259，有效1183
	 * 2、获取某个分类的页数
	 * 3、获取某个分类下的所有分页链接
	 * 4、获取某个商品分类分页下的所有的商品id，链接
	 * 5、获取链接的商品详情
	 */
	
	@Test
	public void run() throws Exception{
		String url = "http://item.jd.com/10112658005.html";
		JDUtil.getItem(url);
	}
	
	//获取所有的三级分类链接
	public static List<String> getAllCat3() throws IOException{
		List<String> catList = new ArrayList<String>();

		//所有分类页面
		String url = "https://www.jd.com/allSort.aspx";
		Document doc = Jsoup.connect(url).get();
		//抓取的内容要自己验证是否正确
		Elements eles = doc.select(".clearfix dd a");
		log.info("三级分类总数:" + eles.size());			//1259
		
		for(Element ele : eles){
			String name = ele.text();
			url = ele.attr("href");
			
			//list.jd.com/list.html?cat=1319,11842,11222&page=264
			if(url.startsWith("//list.jd.com/list.html?cat=")){
				catList.add("http:"+url);
			}
			
			log.info(name+" - "+url);
		}
		log.info("规则三级分类总数:" + catList.size());		//1183
		return catList;
	}
	
	//获取某个分类的页数
	private static Integer getPages(String catUrl){
		try {
			Elements eles = Jsoup.connect(catUrl).get().select("div#J_topPage span.fp-text i");
			Element ele = eles.get(0);
			String count = ele.text();
			log.debug(catUrl+"("+count+")");
			
			return Integer.parseInt(count);
		} catch (Exception e) {
			log.error("[page error] "+catUrl);		//另外的程序搜索error异常，对这些个别的链接重新抓取
			e.printStackTrace();
		}
		return 0;	//忽略掉这个分类，继续爬取
	}
	
	//获取某个分类的所有页的链接
	private static List<String> getCatPageUrl(String catUrl){
		List<String> catPageUrlList = new ArrayList<String>();
		Integer pageNum = getPages(catUrl);
		for(int i=1;i<=pageNum;i++){
			String url = catUrl + "&page="+i;
			log.debug(url);
			catPageUrlList.add(url);
		}
		return catPageUrlList;
	}
	
	//获取一个分页页面的所有的商品的id链接
	public static List<String> getItemUrl(String catPageUrl) throws IOException{
		List<String> itemUrlList = new ArrayList<String>();
		Elements eles = Jsoup.connect(catPageUrl).get()
			.select(".gl-i-wrap")
			.select(".j-sku-item .p-img a[href]");
		for(Element ele :eles){
			//http://item.jd.com/1681290270.html
			String itemUrl = "http:" + ele.attr("href");
			log.debug(itemUrl);
			itemUrlList.add(itemUrl);
		}
		return itemUrlList;
	}
	
	public static Item getItem(String itemUrl){
		Item _item = new Item();
		Long itemId = Long.parseLong(itemUrl.substring(itemUrl.lastIndexOf("/")+1,itemUrl.length()-5));
		_item.setId(itemId);
		_item.setTitle(getTitle(itemUrl));
		_item.setPrice(getItemPrice(itemId));
		//_item.setImage(getImage(itemUrl));
		_item.setSellPoint(getSellPoint(itemId));
		_item.setDesc(getItemDesc(itemId));
		
		log.debug(_item);
		return _item;
	}
	
	//获取某个商品的商品标题
	private static String getTitle(String itemUrl){
		try {
			Document doc = Jsoup.connect(itemUrl).get();
			Element ele = doc.select(".itemInfo-wrap .sku-name").get(0);
			
			return ele.text();
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	
	//获取某个商品的架构
	private static Long getItemPrice(Long itemId){
		try {
			String url = "http://p.3.cn/prices/mgets?skuIds=J_"+itemId;
			String json = Jsoup.connect(url).ignoreContentType(true).execute().body();
			JsonNode jsonNode = MAPPER.readTree(json);
			
			//解析完数组，获取数组第一条数据，获取它的p元素值
			Double price = jsonNode.get(0).get("p").asDouble();
			return Math.round(price*100);	//乘以100，四舍五入
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		return 0L;		
	}
	
	//获取某个商品的图片
	private static String getImage(String itemUrl){
		try {
			Document doc = Jsoup.connect(itemUrl).get();
			Elements eles = doc.select("ul li img");
			String image = "";
			for(Element ele : eles){
				image += ele.attr("src") + ",";
			}
			image = image.substring(0, image.length()-1);
			
			return image;
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}	
	
	//获取某个商品的卖点
	private static String getSellPoint(Long itemId){
		String url = "http://ad.3.cn/ads/mgets?skuids=AD_" + itemId;
		try {
			String json = Jsoup.connect(url).ignoreContentType(true).execute().body();
			JsonNode jsonNode = MAPPER.readTree(json);
			String sellPoint = jsonNode.get(0).get("ad").asText();
			return sellPoint;
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	
	private static String getItemDesc(Long itemId){
		String url = "http://d.3.cn/desc/" + itemId;
		try {
			String jsonp = Jsoup.connect(url).ignoreContentType(true).execute().body();
			String json = jsonp.substring(9, jsonp.length()-1);	//把函数名去掉
			JsonNode jsonNode = MAPPER.readTree(json);
			String desc = jsonNode.get("content").asText();
			return desc;
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
}
