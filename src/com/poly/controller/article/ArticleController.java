package com.poly.controller.article;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.poly.bean.TbArticle;
import com.poly.bean.User;
import com.poly.controller.admin.plate.MasterController;
import com.poly.redis.RedisDao;
import com.poly.service.article.ArticleService;
import com.poly.service.user.UserService;
import com.poly.util.MyConfig;

@Controller
@RequestMapping("/article")
public class ArticleController {
	@Autowired
	public ArticleService articleService;
	@Autowired
	public UserService userService;
	
	@RequestMapping("/add.htm")
	public ModelAndView add(HttpSession session) {
		ModelAndView view = null;
		Map<String, String> urlMap = new LinkedHashMap<String, String>();
		urlMap.put("首页", "xpabc/index/index.htm");
		
		User user = (User) session.getAttribute("user");
		if (user == null) {
			view = new ModelAndView("user/toLogin.ftl");
		}else {
			urlMap.put("用户中心", "xpabc/userCenter/mine/"+ user.getUser_id() +".htm");
			urlMap.put("我的文章", "xpabc/userCenter/myArticles/"+ user.getUser_id() +".htm");
			urlMap.put("发表文章", "xpabc/article/add.htm");
			view = new ModelAndView("article/add.ftl");
			List<Map<String, String>> list = RedisDao.plate_list();
			view.addObject("plateList", list);
			if (user.getRole()==1) {
				view.addObject("user_role", 1);
				List<Map<String, String>> listin = RedisDao.plate_listin();
				view.addObject("plateListin", listin);
			}else {
				view.addObject("user_role", 0);
			}
		}
		view.addObject("urlMap", urlMap);
		return view;
	}
	
	@RequestMapping("/postnew")
	@ResponseBody
	public String postnew(HttpSession session, String title, int plate, int isin, String content){
		User user = (User) session.getAttribute("user");
		if (user == null) {
			return "-1";
		}else {
			if (isin==1 && user.getRole()==0) {
				return "-1";
			}else {
				content = content.replaceAll(MyConfig.article_filter, "");
				int uid = user.getUser_id();
				TbArticle tbArticle = new TbArticle();
				tbArticle.setAdd_time(new Date());
				tbArticle.setArticle_content(content);
				tbArticle.setArticle_title(title);
				tbArticle.setPlate_id(plate);
				tbArticle.setUpdate_time(new Date()); 
				tbArticle.setUser_id(uid);
				tbArticle.setArticle_state(1);
				
				int result = RedisDao.article_save(tbArticle, isin);
				if (result==0) {
					return "0";
				}else {
					return result + "";
				}
			}
		}
	}
	
	@RequestMapping("/error.htm")
	public ModelAndView error() {
		Map<String, String> urlMap = new LinkedHashMap<String, String>();
		urlMap.put("首页", "xpabc/index/index.htm");
		urlMap.put("错误", "xpabc/article/error.htm");
		ModelAndView view = new ModelAndView("article/error.ftl");
		view.addObject("urlMap", urlMap);
		return view;
	}
	
	@RequestMapping("/show/{articleid}.htm")
	public ModelAndView show(@PathVariable("articleid")int articleid, HttpSession session) throws ParseException {
		
		User user = (User) session.getAttribute("user");
		int user_id = 0;
		if (user!=null) {
			user_id  = user.getUser_id();
		}
		
		Map<String, String> urlMap = new LinkedHashMap<String, String>();
		urlMap.put("首页", "xpabc/index/index.htm");
		
		ModelAndView view = null;
		Map<String, String> resultMap = RedisDao.article_getDetail(articleid, user_id);
		if (resultMap==null) {
			view = new ModelAndView("/article/error.ftl");
			urlMap.put("错误", "xpabc/article/error.htm");
		} else {
			
			if (resultMap.get("article_canlook").equals("0")) {
				view = new ModelAndView("article/error.ftl");
				urlMap.put("错误", "xpabc/article/error.htm");
			} else {
				String artids = (String) session.getAttribute("article_looked");
				if (artids==null) {
					RedisDao.article_addLookCount(articleid);
					session.setAttribute("article_looked", "[" + articleid + "]");
				}else if (!artids.contains("["+ articleid +"]")) {
					RedisDao.article_addLookCount(articleid);
					session.setAttribute("article_looked", artids + "[" + articleid + "]");
				}
				
				String add_time_str = resultMap.get("add_time");
				if(!StringUtils.isEmpty(add_time_str)){
					Date add_date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(add_time_str);
					long between = System.currentTimeMillis()-add_date.getTime();
					String time_str = "";
					if(between<60*60*1000){
						time_str = ((Double)Math.floor(between/60/1000)).intValue()+"分钟前";
					}else if(between<24*60*60*1000){
						time_str = Math.floor(between/60/60/1000)+"小时前";
					}else{
						time_str = new SimpleDateFormat("yyyy-MM-dd").format(add_date);
					}
					resultMap.put("add_time", time_str);
				}
				
				view = new ModelAndView("article/show2.ftl");
				view.addObject("artMap", resultMap);
				List<Map<String, Object>> comList = RedisDao.comment_getDetailByArticleId(articleid);
				view.addObject("comList", comList);
				
				if (resultMap.get("article_isin").equals("1")) {
					urlMap.put("项目日志", "xpabc/index/inlearnindex.htm");
				}else {
					urlMap.put("学习交流", "xpabc/index/learnindex.htm");
				}
				
				boolean is_master = false;
				if (user_id>0) {
					is_master = RedisDao.plate_masterCheckIsMaster(user.getUser_id(), Integer.parseInt(resultMap.get("plate_id")));
				}
				
				view.addObject("is_master", is_master);
				Map<String,String> sizeMap = userService.getUserSize(Integer.parseInt(resultMap.get("user_id")));
				String user_elite_size = sizeMap.get("user_elite_size");
				view.addObject("user_elite_size", user_elite_size);
				
				urlMap.put(resultMap.get("article_title"), "xpabc/article/show/"+ articleid +".htm");
			}
		}
		view.addObject("urlMap", urlMap);
		return view;
	}
	
	@RequestMapping("/edit/{articleid}.htm")
	public ModelAndView edit(@PathVariable("articleid")int articleid, HttpSession session) {
		Map<String, String> urlMap = new LinkedHashMap<String, String>();
		urlMap.put("首页", "xpabc/index/index.htm");
		
		ModelAndView view = null;
		User user = (User) session.getAttribute("user");
		if (user == null) {
			view = new ModelAndView("user/toLogin.ftl");
		}else {
			view = new ModelAndView("article/edit.ftl");
			Map<String, String> map = RedisDao.article_getContent(user.getUser_id(), articleid);
			if (map!=null) {
				view.addObject("isGet", 1);
				view.addObject("artMap", map);
				if (map.get("is_master").equals("0")) {
					urlMap.put("用户中心", "xpabc/userCenter/mine/"+ user.getUser_id() +".htm");
					urlMap.put("我的文章", "xpabc/userCenter/myArticles/"+ user.getUser_id() +".htm");
				} else {
					urlMap.put("文章", "xpabc/article/show/"+ articleid +".htm");
				}
				
				urlMap.put(map.get("article_title"), "xpabc/article/edit/"+ map.get("article_id") +".htm");
			}else {
				urlMap.put("错误", "xpabc/article/error.htm");
				view = new ModelAndView("article/error.ftl");
			}
		}
		view.addObject("urlMap", urlMap);
		return view;
	}
	
	@RequestMapping("/update")
	@ResponseBody
	public String update(HttpSession session, int articleid, String content){
		User user = (User) session.getAttribute("user");
		if (user == null) {
			return "-1";
		}else {
			int uid = user.getUser_id();
			TbArticle tbArticle = new TbArticle();
			tbArticle.setArticle_id(articleid);
			tbArticle.setArticle_content(content);
			tbArticle.setUpdate_time(new Date());
			
			int result = RedisDao.article_update(uid, tbArticle);
			
			if (result==0) {
				return "0";
			}else {
				return result + "";
			}
		}
	}
	
	@RequestMapping("/sql")
	public void sqltest() throws ParseException{
		articleService.persistentArticle();
	}
	
}
