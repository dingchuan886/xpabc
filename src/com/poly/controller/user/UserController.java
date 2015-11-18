package com.poly.controller.user;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.poly.bean.Message;
import com.poly.bean.PhoneCode;
import com.poly.bean.User;
import com.poly.controller.BaseController;
import com.poly.redis.RedisDao;
import com.poly.util.MD5Util;
import com.poly.util.ObjectToMapUtil;
import com.poly.util.PhoneMessageUtil;
import com.poly.util.PostMsg;
import com.poly.util.SMSjianzhou;

@Controller("userController")
@RequestMapping("/user")
public class UserController extends BaseController{

	/**
	 * 进入登录界面
	 * @return
	 */
	@RequestMapping("/toLogin")
	public ModelAndView toLogin(String fromUrl){
		ModelAndView mv = new ModelAndView("user/toLogin.ftl");
		if(StringUtils.isEmpty(fromUrl)){
			fromUrl = "";
		}
		mv.addObject("fromUrl", fromUrl);
		return mv;
	}
	/**
	 * 用户登录
	 * @param username
	 * @param password
	 * @param session
	 * @return
	 */
	@RequestMapping("/login")
	public @ResponseBody Message login(String username,String password,HttpSession session){
		Message msg = new Message();
		try{
			if(StringUtils.isEmpty(username)){
				msg.setCode(errorCode);
				msg.setMsg("用户名不能为空!");
				return msg;
			}
			if(StringUtils.isEmpty(password)){
				msg.setCode(errorCode);
				msg.setMsg("密码不能为空!");
				return msg;
			}
			User user  = RedisDao.getUserByUsername(username);
			if(user!=null&&user.getUser_state()==1){
				msg.setCode(errorCode);
				msg.setMsg("该账号已被锁定!");
				return msg;
			}
			if(user==null||user.getUser_state()!=0||!MD5Util.string2MD5(password).equals(user.getUser_password())){
				user = RedisDao.getUserByPhone(username);
				if(user!=null&&user.getUser_state()==1){
					msg.setCode(errorCode);
					msg.setMsg("该账号已被锁定!");
					return msg;
				}
				if(user==null||!MD5Util.string2MD5(password).equals(user.getUser_password())){
					msg.setCode(errorCode);
					msg.setMsg("用户名或密码错误!");
					return msg;
				}
//				msg.setCode(errorCode);
//				msg.setMsg("用户名或密码错误!");
//				return msg;
			}
			user.setLog_time(new Date());
			int id = RedisDao.saveUser(user);
			RedisDao.addUserUpdateList(id);
			session.setAttribute("user", user);
			msg.setCode(successCode);
			msg.setMsg("登录成功！");
			return msg;
		}catch(Exception e){
			msg.setCode(errorCode);
			msg.setMsg("系统异常!");
			return msg;
		}
	}
	/**
	 * 进入注册页面
	 * @return
	 */
	@RequestMapping("/toRegister.htm")
	public ModelAndView toRegister(){
		ModelAndView mv = new ModelAndView("user/toRegister.ftl");
		return mv;
		
	}
	/**
	 *用户注册
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping("/register")
	public@ResponseBody Message register(HttpSession session,String username,String password,
			String password2,String phone,String code) throws Exception{
		Message msg = new Message();
		if(StringUtils.isEmpty(username)){
			msg.setCode(errorCode);
			msg.setMsg("用户名不能为空！");
			return msg;
		}
		
		if(StringUtils.isEmpty(password)){
			msg.setCode(errorCode);
			msg.setMsg("密码不能为空！");
			return msg;
		}
		if(StringUtils.isEmpty(phone)){
			msg.setCode(errorCode);
			msg.setMsg("手机号不能为空！");
			return msg;
		}
		if(StringUtils.isEmpty(code)){
			msg.setCode(errorCode);
			msg.setMsg("验证码不能为空！");
			return msg;
		}
		if(!password.equals(password2)){
			msg.setCode(errorCode);
			msg.setMsg("两次输入密码不一致！");
			return msg;
		}
		Map<String,String> phoneCode = RedisDao.getUserCodeMapByPhone(phone);
		if(phoneCode==null||phoneCode.size()<=0){
			msg.setCode(errorCode);
			msg.setMsg("请先获取验证码！");
			return msg;
		}
		String sendTime = phoneCode.get("sendtime");
		if(sendTime!=null){
			long time_betwwen = new Date().getTime() - new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(sendTime).getTime();
			if(time_betwwen/1000>120){
				msg.setCode(errorCode);
				msg.setMsg("验证码已失效！");
				return msg;
			}
		}else{
			msg.setCode(errorCode);
			msg.setMsg("验证码已失效！");
			return msg;
		}
		if(!code.equals(phoneCode.get("code"))){
			msg.setCode(errorCode);
			msg.setMsg("验证码错误！");
			return msg;
		}
		User user = RedisDao.getUserByPhone(phone);
		if(user!=null&&user.getUser_state()!=9){
			msg.setCode(errorCode);
			msg.setMsg("该手机号已注册！");
			return msg;
		}
		user = RedisDao.getUserByUsername(username);
		if(user!=null&&user.getUser_state()!=9){
			msg.setCode(errorCode);
			msg.setMsg("该用户名已被注册！");
			return msg;
		}
		user = new User();
		user.setUser_grade(0);
		user.setReg_time(new Date());
		user.setLog_time(new Date());
		user.setUser_nickname("");
		user.setUser_password(MD5Util.string2MD5(password));
		user.setUser_phone(phone);
		user.setUser_photo("");
		user.setUser_name(username);
		user.setUser_state(0);
		user.setRole(0);;
		int id = RedisDao.saveUser(user);
		RedisDao.addUserInsertList(id);
		session.setAttribute("user", user);
		msg.setCode(successCode);
		msg.setMsg("注册成功!");
		return msg;
		
	}
	/**
	 * 获取手机验证码
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping("/getPhoneCode")
	public @ResponseBody Message getPhoneCode(String phone) throws Exception{
		Message msg = new Message();
		int a=(int)(Math.random()*100)+1;
		int b=(int)(Math.random()*100)+1;
		String aStr="";
		String bStr="";
		if(String.valueOf(a).length()==1){
			aStr="00"+a;
		}else if(String.valueOf(a).length()==2){
			aStr="0"+a;
		}else{
			aStr=String.valueOf(a);
		}
		if(String.valueOf(b).length()==1){
			bStr="00"+b;
		}else if(String.valueOf(b).length()==2){
			bStr="0"+b;
		}else{
			bStr=String.valueOf(b);
		}
		String code=aStr+bStr;
		String content = "验证码:"+code;
		int back = PostMsg._HttpPost(phone, content);
		//int flag = SMSjianzhou.sendPhoneMsg(phone, content);
		if(back==1){
			msg.setCode("0000");
			msg.setMsg("");
            PhoneCode phoneCode = new PhoneCode();
			phoneCode.setCode(code);
			phoneCode.setPhone(phone);
			phoneCode.setSendTime(new Date());
			Map<String,String> mp=ObjectToMapUtil.changeToMap(phoneCode);
			RedisDao.updateUserCodeByPhone(phone, mp);	
		}else{
			msg.setCode("1111");
			msg.setMsg("发送短信失败");
		}
		return msg;
	}
	/**
	 * 检查用户名是否已被注册
	 * @param username
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/checkUsername")
	public @ResponseBody Message checkUsername (String username) throws Exception{
		Message msg = new Message();
		User user = RedisDao.getUserByUsername(username);
		if(user!=null&&user.getUser_state()!=9){
			msg.setCode(errorCode);
			msg.setMsg("用户名已存在");
			return msg;
		}
		msg.setCode(successCode);
		msg.setMsg("用户名可以注册");
		return msg;
		
	}
	/**
	 * 注销
	 * @param username
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/logout")
	public @ResponseBody Message logout (HttpSession session) throws Exception{
		Message msg = new Message();
		session.removeAttribute("user");
		msg.setCode(successCode);
		msg.setMsg("注销成功!");
		return msg;
		
	}
	/**
	 * 检查手机号是否已被注册
	 * @param username
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/checkPhone")
	public @ResponseBody Message checkPhone (String phone) throws Exception{
		Message msg = new Message();
		User user = RedisDao.getUserByPhone(phone);
		if(user!=null&&user.getUser_state()!=9){
			msg.setCode(errorCode);
			msg.setMsg("手机号已注册");
			return msg;
		}
		msg.setCode(successCode);
		msg.setMsg("手机号可以注册");
		return msg;
		
	}
	
}
