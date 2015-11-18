package com.poly.controller.index;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.poly.bean.Message;
import com.poly.bean.User;
import com.poly.controller.BaseController;
import com.poly.controller.user.UserCenterController;
import com.poly.redis.RedisDao;
import com.poly.util.PhoneMessageUtil;

@Controller("indexController")
@RequestMapping("/index")
public class IndexController extends BaseController{
	@Resource(name="userCenterController")
	UserCenterController userCenterController;
	/**
	 * 进入主页
	 * @return
	 */
	@RequestMapping("/index.htm")
	public ModelAndView toindex(){
		ModelAndView mv = new ModelAndView("bbs/index_bbs.ftl");
		List<Map<String,String>> urlList=new ArrayList<Map<String,String>>();
		List<Map<String,Object>> list=RedisDao.getallplateAndarticles();
		List<Map<String,Object>> hotList=RedisDao.gethotArticles();
		List<Map<String,Object>> jinghuaList=RedisDao.getjinghuaArticles();
		mv.addObject("articleLists",list);
		mv.addObject("hotarticleLists",hotList);
		mv.addObject("jinghuaLists",jinghuaList);
		Map<String,String> map=new HashMap<String, String>();
		map.put("url", "xpabc/index/index.htm");
		map.put("urlname", "首页");
		urlList.add(map);
		mv.addObject("urlList",urlList);
		return mv;
	}
	/**
	 * 学习交流
	 * @return
	 */
	@RequestMapping("/learnindex.htm")
	public ModelAndView tolearnindex(){
		ModelAndView mv = new ModelAndView("bbs/bbs_learn.ftl");
		List<Map<String,String>> urlList=new ArrayList<Map<String,String>>();
		List<Map<String,Object>> list=RedisDao.getallplateAndarticlesForlearn();
		List<Map<String,Object>> jinghuaList=RedisDao.getjinghuaArticles();
		mv.addObject("articleLists",list);
		mv.addObject("jinghuaLists",jinghuaList);
		Map<String,String> map=new HashMap<String, String>();
		map.put("url", "xpabc/index/learnindex.htm");
		map.put("urlname", "学习交流");
		urlList.add(map);
		mv.addObject("urlList",urlList);
		return mv;
	}
	/**
	 * 内部交流
	 * @return
	 */
	@RequestMapping("/inlearnindex.htm")
	public ModelAndView toinlearnindex(){
		ModelAndView mv = new ModelAndView("bbs/bbs_inlearn.ftl");
		List<Map<String,String>> urlList=new ArrayList<Map<String,String>>();
		List<Map<String,Object>> list=RedisDao.getallplateAndarticlesForInlearn();
		mv.addObject("articleLists",list);
		Map<String,String> map=new HashMap<String, String>();
		map.put("url", "xpabc/index/inlearnindex.htm");
		map.put("urlname", "项目日志");
		urlList.add(map);
		mv.addObject("urlList",urlList);
		return mv;
	}
	@RequestMapping("/tobbsList/{plateid}/{page}.htm")
	public ModelAndView tobbsList(@PathVariable("plateid")String plateid,@PathVariable("page")Integer page,HttpSession session){
		User user = (User) session.getAttribute("user");
		System.out.println(plateid);
		List<Map<String,String>> urlList=new ArrayList<Map<String,String>>();
		if(page==null){
			page=1;
		}
		ModelAndView mv = new ModelAndView("bbs/bbs_list.ftl");
		List<String> cidsList=new ArrayList<String>();
		List<Map<String,String>> plateList=new ArrayList<Map<String,String>>();
		plateList=RedisDao.getallplate();
		if(user==null||user.getRole()==0){
//		   plateList=RedisDao.getallplate();
		   cidsList=RedisDao.getallarticlesidByplateid(plateid);
		}
		else{
//			plateList=RedisDao.getallAndinplate();
			cidsList=RedisDao.getallAndinarticlesidByplateid(plateid);
		}
		int pages = 1;
		int p = cidsList.size();
		if(p>0){
			if(p%articlePageSize==0){
				pages = p/articlePageSize;
			}else{
				pages = p/articlePageSize+1;
			}
			if(page>=pages){
				page=pages;
			}
			int start = (page-1)*articlePageSize;
			int end = start+articlePageSize-1;
			if(end>=p){
				end = p-1;
			}
			List<Map<String,String>> list=RedisDao.getarticlesbyplateid(cidsList,start,end);
			if(plateid.equals("0")){
				mv.addObject("platename","全部帖子");	
				mv.addObject("plateid","0");	
				Map<String,String> map1=new HashMap<String, String>();
				map1.put("url", "xpabc/index/learnindex.htm");
				map1.put("urlname", "学习交流");
				urlList.add(map1);
				Map<String,String> map=new HashMap<String, String>();
				map.put("url", "xpabc/index/tobbsList/0/1.htm");
				map.put("urlname", "全部帖子");
				urlList.add(map);
				mv.addObject("urlList",urlList);
			}
			else{
				String platename=RedisDao.getplatenamebyid(plateid);
				mv.addObject("platename",platename);	
				mv.addObject("plateid",plateid);
				Map<String,String> map1=new HashMap<String, String>();
				map1.put("url", "xpabc/index/learnindex.htm");
				map1.put("urlname", "学习交流");
				urlList.add(map1);
				Map<String,String> map=new HashMap<String, String>();
				map.put("url", "xpabc/index/tobbsList/"+plateid+"/1.htm");
				map.put("urlname",platename);
				urlList.add(map);
				mv.addObject("urlList",urlList);
			}
			mv.addObject("page", page.intValue());
			mv.addObject("pages",pages);
			mv.addObject("articleLists",list);
			mv.addObject("plateList",plateList);
		}
		else{
			if(plateid.equals("0")){
				mv.addObject("platename","全部帖子");	
				mv.addObject("plateid","0");	
				Map<String,String> map1=new HashMap<String, String>();
				map1.put("url", "xpabc/index/learnindex.htm");
				map1.put("urlname", "学习交流");
				urlList.add(map1);
				Map<String,String> map=new HashMap<String, String>();
				map.put("url", "xpabc/index/tobbsList/0/1.htm");
				map.put("urlname", "全部帖子");
				urlList.add(map);
				mv.addObject("urlList",urlList);
			}
			else{
				String platename=RedisDao.getplatenamebyid(plateid);
				mv.addObject("platename",platename);		
				mv.addObject("plateid",plateid);	
				Map<String,String> map1=new HashMap<String, String>();
				map1.put("url", "xpabc/index/learnindex.htm");
				map1.put("urlname", "学习交流");
				urlList.add(map1);
				Map<String,String> map=new HashMap<String, String>();
				map.put("url", "xpabc/index/tobbsList/"+plateid+"/1.htm");
				map.put("urlname",platename);
				urlList.add(map);
				mv.addObject("urlList",urlList);
			}
			mv.addObject("page", page.intValue());
			mv.addObject("pages",pages);
			mv.addObject("articleLists",new ArrayList<Map<String,String>>());
			mv.addObject("plateList",plateList);
		}
		return mv;
	}
	@RequestMapping("/tobbsUserList/{plateid}/{userid}/{page}.htm")
	public ModelAndView tobbsUserList(HttpSession session,@PathVariable("plateid")String plateid,@PathVariable("userid")String userid,@PathVariable("page")Integer page){
		List<Map<String,String>> urlList=new ArrayList<Map<String,String>>();
		User user = (User) session.getAttribute("user");
		if(user!=null&&user.getUser_id()==Integer.parseInt(userid)){
			return userCenterController.mine(session, 1);
		}
		System.out.println(plateid);
		if(page==null){
			page=1;
		}
		ModelAndView mv = new ModelAndView("bbs/bbs_userlist.ftl");
		List<String> cidsList=new ArrayList<String>();
		List<Map<String,String>> plateList=new ArrayList<Map<String,String>>();
		 plateList=RedisDao.getallplate();
		if(user==null||user.getRole()==0){
//			   plateList=RedisDao.getallplate();
			   cidsList=RedisDao.getallarticlesidByplateidAnduserid(plateid,userid);
			}
		else{
//				plateList=RedisDao.getallAndinplate();
				cidsList=RedisDao.getallAndinarticlesidByplateidAnduserid(plateid,userid);
			}
		Map<String,String> map=RedisDao.getUserMessage(userid);
		int pages = 1;
		int p = cidsList.size();
		if(p>0){
			if(p%articlePageSize==0){
				pages = p/articlePageSize;
			}else{
				pages = p/articlePageSize+1;
			}
			if(page>=pages){
				page=pages;
			}
			int start = (page-1)*articlePageSize;
			int end = start+articlePageSize-1;
			if(end>=p){
				end = p-1;
			}
			List<Map<String,String>> list=RedisDao.getarticlesbyplateidAnduserid(cidsList,userid,start,end);
			if(plateid.equals("0")){
				mv.addObject("platename","全部帖子");	
				mv.addObject("plateid","0");
				Map<String,String> map1=new HashMap<String, String>();
				map1.put("url", "xpabc/index/learnindex.htm");
				map1.put("urlname", "学习交流");
				urlList.add(map1);
				Map<String,String> map2=new HashMap<String, String>();
				map2.put("url", "xpabc/index/tobbsUserList/0/"+userid+"/1.htm");
				map2.put("urlname", map.get("user_name"));
				urlList.add(map2);
				Map<String,String> map3=new HashMap<String, String>();
				map3.put("url", "xpabc/index/tobbsUserList/0/"+userid+"/1.htm");
				map3.put("urlname", "全部帖子");
				urlList.add(map3);
				mv.addObject("urlList",urlList);
				mv.addObject("urlListsize",urlList.size());
			}
			else{
				mv.addObject("platename",RedisDao.getplatenamebyid(plateid));	
				mv.addObject("plateid",plateid);	
				Map<String,String> map1=new HashMap<String, String>();
				map1.put("url", "xpabc/index/learnindex.htm");
				map1.put("urlname", "学习交流");
				urlList.add(map1);
				Map<String,String> map2=new HashMap<String, String>();
				map2.put("url", "xpabc/index/tobbsUserList/0/"+userid+"/1.htm");
				map2.put("urlname", map.get("user_name"));
				urlList.add(map2);
				Map<String,String> map3=new HashMap<String, String>();
				map3.put("url", "xpabc/index/tobbsUserList/"+plateid+"/"+userid+"/1.htm");
				map3.put("urlname", RedisDao.getplatenamebyid(plateid));
				urlList.add(map3);
				mv.addObject("urlList",urlList);
				mv.addObject("urlListsize",urlList.size());
			}
			mv.addObject("page", page.intValue());
			mv.addObject("pages",pages);
			mv.addObject("articleLists",list);
			mv.addObject("plateList",plateList);
		}
		else{
			if(plateid.equals("0")){
				mv.addObject("platename","全部帖子");	
				mv.addObject("plateid","0");	
				Map<String,String> map1=new HashMap<String, String>();
				map1.put("url", "xpabc/index/learnindex.htm");
				map1.put("urlname", "学习交流");
				urlList.add(map1);
				Map<String,String> map2=new HashMap<String, String>();
				map2.put("url", "xpabc/index/tobbsUserList/0/"+userid+"/1.htm");
				map2.put("urlname", map.get("user_name"));
				urlList.add(map2);
				Map<String,String> map3=new HashMap<String, String>();
				map3.put("url", "xpabc/index/tobbsUserList/0/"+userid+"/1.htm");
				map3.put("urlname", "全部帖子");
				urlList.add(map3);
				mv.addObject("urlList",urlList);
			}
			else{
				mv.addObject("platename",RedisDao.getplatenamebyid(plateid));	
				mv.addObject("plateid",plateid);	
				Map<String,String> map1=new HashMap<String, String>();
				map1.put("url", "xpabc/index/learnindex.htm");
				map1.put("urlname", "学习交流");
				urlList.add(map1);
				Map<String,String> map2=new HashMap<String, String>();
				map2.put("url", "xpabc/index/tobbsUserList/0/"+userid+"/1.htm");
				map2.put("urlname", map.get("user_name"));
				urlList.add(map2);
				Map<String,String> map3=new HashMap<String, String>();
				map3.put("url", "xpabc/index/tobbsUserList/"+plateid+"/"+userid+"/1.htm");
				map3.put("urlname", RedisDao.getplatenamebyid(plateid));
				urlList.add(map3);
				mv.addObject("urlList",urlList);
			}
			
			mv.addObject("page", page.intValue());
			mv.addObject("pages",pages);
			mv.addObject("articleLists",new ArrayList<Map<String,String>>());
			mv.addObject("plateList",plateList);
			mv.addObject("userid",userid);
		}
		mv.addObject("username",map.get("user_name"));
		mv.addObject("userphoto",map.get("user_photo"));
		mv.addObject("count",map.get("count"));
		mv.addObject("urlList",urlList);
		return mv;
	}
	
}
