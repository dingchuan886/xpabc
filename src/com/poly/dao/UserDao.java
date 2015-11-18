package com.poly.dao;


import javax.annotation.Resource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.poly.bean.User;
@Component("userDao")
public class UserDao {
	@Resource(name="jdbc")
	JdbcTemplate jdbcTemplate;
	public void insert(User user){
		String sql = "insert into tb_user (user_id,user_name,user_password,user_nickname,user_phone,user_grade,user_photo,reg_time,log_time,user_state,role) values(?,?,?,?,?,?,?,?,?,?,?)";
		Object[] args = new Object[]{user.getUser_id(),user.getUser_name(),user.getUser_password(),user.getUser_nickname(),
				user.getUser_phone(),user.getUser_grade(),user.getUser_photo(),user.getReg_time(),user.getLog_time(),user.getUser_state(),user.getRole()};
		jdbcTemplate.update(sql, args);
	}
	public void update(User user){
		String sql = "update tb_user set user_name=?,user_password=?,user_nickname=?,user_phone=?,user_grade=?,user_photo=?,reg_time=?,log_time=?,user_state=?,role=? where user_id=?";
		Object[] args = new Object[]{user.getUser_name(),user.getUser_password(),user.getUser_nickname(),user.getUser_phone(),
				user.getUser_grade(),user.getUser_photo(),user.getReg_time(),user.getLog_time(),user.getUser_state(),user.getRole(),user.getUser_id()};
		jdbcTemplate.update(sql, args);
	}
}
