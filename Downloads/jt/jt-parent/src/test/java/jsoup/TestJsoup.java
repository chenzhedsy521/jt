package jsoup;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestJsoup {
	private static final Logger log = Logger.getLogger(TestJsoup.class);
	private static final ObjectMapper MAPPER = new ObjectMapper();
	
	@Test	 //抓整个页面
	public void html() throws IOException{
		String url = "http://tech.qq.com/a/20170330/003855.htm";
		//doc代表一个页面
		String html = Jsoup.connect(url).execute().body();
		System.out.println(html);
	}
	
	@Test 	//抓整站，找到所有a链接，然后进行广度优先/深度优先进行遍历
	public void getAllATag() throws IOException{
		String url = "http://tech.qq.com/a/20170330/003855.htm";
		//获取到页面
		Document doc = Jsoup.connect(url).get();
		//获取到页面中的所有a标签
		Elements eles = doc.getElementsByTag("a");
		for(Element ele : eles){
			String title = ele.text();		//获取a标签的内容
			String aurl = ele.attr("href");	//获取a标签的属性
			log.debug(title+" - "+aurl);
		}
	}
	
	@Test	//商品标题
	public void getItemTile() throws IOException{
		String url = "https://item.jd.com/3882469.html";
		Document doc = Jsoup.connect(url).get();
		Element ele = doc.select(".itemInfo-wrap .sku-name").get(0);
		String title = ele.text();
		log.debug(title);
	}
	
	@Test	//当当商城，商品标题
	public void getDDItemTile() throws IOException{
		String url = "http://product.dangdang.com/1052875306.html";
		Document doc = Jsoup.connect(url).get();
		Element ele = doc.select("article").get(0);
		String title = ele.text();
		log.debug(title);
	}
	
	@Test	//价格
	public void getItemPrice() throws IOException{
		String url = "http://p.3.cn/prices/mgets?skuIds=J_3882469";
		String json = Jsoup.connect(url).ignoreContentType(true).execute().body();
		JsonNode jsonNode = MAPPER.readTree(json);
		
		//解析完数组，获取数组第一条数据，获取它的p元素值
		Double price = jsonNode.get(0).get("p").asDouble();
		log.debug(price);
	}
	
	@Test	//商品描述
	public void getItemDesc() throws IOException{
		String url = "http://d.3.cn/desc/3882469";
		String jsonp = Jsoup.connect(url).ignoreContentType(true).execute().body();
		String json = jsonp.substring(9, jsonp.length()-1);	//把函数名去掉
		JsonNode jsonNode = MAPPER.readTree(json);
		
		String desc = jsonNode.get("content").asText();
		log.debug(desc);
	}
}
