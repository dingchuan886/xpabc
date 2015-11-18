package com.poly.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.poly.bean.User;

public class UserUtil {
	public static User mapToUser(Map<String,String> map) throws Exception{
		User user = null;
		if(map!=null && map.size()>=0){
			user  = new User();
			Integer id = Integer.parseInt(map.get("user_id"));
			String username = map.get("user_name");
			String password = map.get("user_password");
			String phone = map.get("user_phone");
			String gradeStr = map.get("user_grade");
			Integer grade = 0;
			if(StringUtils.isNotEmpty(gradeStr)){
				grade = Integer.parseInt(gradeStr);
			}
			String photo = map.get("user_photo");
			String nickname = map.get("user_nickname");
			
			String stateStr = map.get("user_state");
			Integer state = 0;
			if(StringUtils.isNotEmpty(stateStr)){
				state = Integer.parseInt(stateStr);
			}
			String joinTimeStr = map.get("reg_time");
			Date joinTime = null;
			if(StringUtils.isNotEmpty(joinTimeStr)){
				joinTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(joinTimeStr);
			}
			String lastLoginTimeStr = map.get("log_time");
			Date lastLoginTime = null;
			if(StringUtils.isNotEmpty(lastLoginTimeStr)){
				lastLoginTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(lastLoginTimeStr);
			}
			Integer role = 0;
			String roleStr = map.get("role");
			if(StringUtils.isNotEmpty(roleStr)){
				role = Integer.parseInt(roleStr);
			}
			user.setUser_grade(grade);
			user.setUser_id(id);
			user.setReg_time(joinTime);
			user.setLog_time(lastLoginTime);
			user.setUser_nickname(nickname);
			user.setUser_password(password);
			user.setUser_phone(phone);
			user.setUser_photo(photo);
			user.setUser_name(username);
			user.setUser_state(state);
			user.setRole(role);
		}
		return user;
	}
}
