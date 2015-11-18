package com.poly.controller.admin.user;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.poly.bean.Message;
import com.poly.bean.User;
import com.poly.redis.RedisDao;
import com.poly.service.user.UserService;

@Controller("adminUserController")
@RequestMapping("/admin/user")
public class UserController {
	@Resource(name="userService")
	UserService userService;
	@RequestMapping("/show.htm")
	public ModelAndView show(Integer page,Integer role,Integer state,String username) throws Exception{
		ModelAndView mv = new ModelAndView("admin/user/show.ftl");
		int pagesize = 20;
		List<User> users = RedisDao.getUserList(role,state,username);
		int size = users.size();
		int pages = 0;
		if(size%pagesize==0){
			pages = size/pagesize;
		}else{
			pages = size/pagesize+1;
		}
		if(page==null||page<=0){
			page=1;
		}
		if(pages<page){
			pages = page;
		}
		int begin = (page-1)*pagesize;
		if(begin<0){
			begin=0;
		}
		int end = begin + pagesize;
		if(end>=users.size()){
			end = users.size()-1;
		}
		List<User> users2 = new ArrayList<User>();
		for(int i = begin;i<=end;i++){
			users2.add(users.get(i));
		}
		mv.addObject("users", users2);
		mv.addObject("page",page);
		mv.addObject("pages",pages);
		mv.addObject("username",username);
		mv.addObject("role",role);
		mv.addObject("state",state);
		return mv;
	}
	@RequestMapping("/getUserInfo")
	public @ResponseBody User getUserInfo(Integer user_id) throws Exception{
		User user = userService.getUserInfo(user_id);
		return user;
	}
	@RequestMapping("/editUser")
	public @ResponseBody User editUser(User user) throws Exception{
		User u = userService.getUserInfo(user.getUser_id());
		userService.editUser(user,u);
		return user;
	}
	@RequestMapping("/deleteUser")
	public @ResponseBody Message deleteUser(Integer user_id){
		Message msg = userService.deleteUser(user_id);
		return msg;
	}
	@RequestMapping("/init")
	public @ResponseBody String init() throws Exception{
		userService.addList();
		return "success";
	}
	@RequestMapping("/recoverUser")
	public @ResponseBody Message  recoverUser(Integer user_id){
		Message msg = userService.recoverUser(user_id);
		return msg;
	}
	@RequestMapping("/removeUser")
	public @ResponseBody Message  removeUser(Integer user_id){
		Message msg = userService.removeUser(user_id);
		return msg;
	}
}
