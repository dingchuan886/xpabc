package com.poly.service.user;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;

import com.poly.bean.Message;
import com.poly.bean.User;
import com.poly.dao.UserDao;
import com.poly.redis.RedisDao;
import com.poly.redis.RedisManager;
import com.poly.util.ObjectToMapUtil;
import com.poly.util.UserUtil;

@Service("userService")
public class UserService {
	@Resource(name="userDao")
	private UserDao userDao;
	
	public void persistentUser() throws Exception{
		Jedis jedis = RedisManager.getJedisObject();
	    try {
	    	String key_insert = "tb_user:user_id:insert";
	    	String key_update = "tb_user:user_id:update";
	    	Set<String>insert_Ids = jedis.zrevrange(key_insert,0,-1);
	    	for(String iid:insert_Ids){
	    		Map<String,String> userMap = jedis.hgetAll("tb_user:user_id:"+iid);
	    		User user = UserUtil.mapToUser(userMap);
	    		userDao.insert(user);
	    		jedis.zrem(key_insert, iid);
	    	}
	    	Set<String>update_Ids = jedis.zrevrange(key_update,0,-1);
	    	for(String uid:update_Ids){
	    		Map<String,String> userMap = jedis.hgetAll("tb_user:user_id:"+uid);
	    		User user = UserUtil.mapToUser(userMap);
	    		userDao.update(user);
	    		jedis.zrem(key_update, uid);
	    	}
	    } finally {
	    	RedisManager.recycleJedisOjbect(jedis);
	    }
		
	}

	public void addList() throws Exception {
		Jedis jedis = RedisManager.getJedisObject();
	    try {
	    	Set<String>keys = jedis.keys("tb_user:user_name*");
	    	for(String key:keys){
	    		String id = jedis.get(key);
	    		if(!StringUtils.isEmpty(id)&&!"null".equals(id)){
	    			Map<String,String> umap = jedis.hgetAll("tb_user:user_id:"+id);
	    			if(umap==null||umap.size()<=0||StringUtils.isEmpty(umap.get("user_id"))){
	    				jedis.del(key);
	    				jedis.zrem("tb_user:user_id:insert", id);
	    				jedis.zrem("tb_user:list:all", id);
	    				jedis.zrem("tb_user:list:user_state:0", id);
	    				jedis.zrem("tb_user:list:role:0", id);
	    				continue;
	    			}
//	    			if(umap.get("user_state").equals("1")){
//	    				jedis.del("tb_user:user_id:"+id);
//	    				jedis.zrem("tb_user:list:role:0", id);
//	    				jedis.zrem("tb_user:list:all", id);
//	    				jedis.zrem("tb_user:user_id:insert", id);
//	    				jedis.zrem("tb_user:user_id:update", id);
//	    				jedis.zrem("tb_user:list:user_state:0", id);
//	    				jedis.zrem("tb_user:list:user_state:1", id);
//	    				
//	    			}else if(umap.get("user_state").equals("0")){
//	    				String phone = umap.get("user_phone");
//		    			if(StringUtils.isNotEmpty(phone)){
//		    				jedis.set("tb_user:user_phone:"+phone, id);
//		    			}
//		    			String username = umap.get("user_name");
//		    			if(StringUtils.isNotEmpty(username)){
//		    				jedis.set("tb_user:user_name:"+username, id);
//		    			}
//	    			}
//	    			String phone = umap.get("user_phone");
//	    			if(StringUtils.isNotEmpty(phone)){
//	    				jedis.set("tb_user:user_phone:"+phone, id);
//	    			}
//	    			String username = umap.get("user_name");
//	    			if(StringUtils.isNotEmpty(username)){
//	    				jedis.set("tb_user:user_name:"+username, id);
//	    			}
//	    			User user = UserUtil.mapToUser(umap);
//	    			umap = ObjectToMapUtil.changeToMap(user);
//	    			jedis.hmset("tb_user:user_id:"+id,umap);
//	    			jedis.zadd("tb_user:user_id:insert",new Date().getTime(), id);
//	    			jedis.zadd("tb_user:list:all",new Date().getTime(), id);
//	    			jedis.zadd("tb_user:list:user_state:0",0,id);
//	    			jedis.zadd("tb_user:list:role:0",0,id);
	    			
	    		}
	    	}
	    	
	    } finally {
	    	RedisManager.recycleJedisOjbect(jedis);
	    }
		
	}
	public User getUserInfo(Integer user_id) throws Exception{
		if(user_id==null){
			return null;
		}
		Map<String, String> umap = RedisDao.getUserByUserId(user_id.toString());
		User user = UserUtil.mapToUser(umap);
		return user;
	}
	public static void main(String[] args) throws Exception {
		new UserService().addList();
	}

	public void editUser(User user, User uold) {
		RedisDao.editUser(user,uold);
		
	}

	public Message deleteUser(Integer user_id) {
		Message msg = new Message();
		RedisDao.deleteUser(user_id);
		RedisDao.addUserUpdateList(user_id);
		msg.setCode("0000");
		msg.setMsg("锁定成功");
		return msg;
	}
	public Map<String,String> getUserSize(int user_id){
		Map<String,String> map = new HashMap<String,String>();
		int articleSize = RedisDao.getArticleSizeByUserId(user_id);
		int eliteSize = RedisDao.getEliteArticleIdListByUserId(user_id)==null?0:RedisDao.getEliteArticleIdListByUserId(user_id).size();
		map.put("user_article_size", articleSize+"");
		map.put("user_elite_size", eliteSize+"");
		return map;
	}

	public Message recoverUser(Integer user_id) {
		Message msg = new Message();
		int back = RedisDao.recoverUser(user_id);
		if(back==1){
			msg.setCode("0000");
			msg.setMsg("解锁成功");
			RedisDao.addUserUpdateList(user_id);
		}else{
			msg.setCode("1111");
			msg.setMsg("解锁失败");
		}
		return msg;
	}

	public Message removeUser(Integer user_id) {
		Message msg = new Message();
		int back = RedisDao.removeUser(user_id);
		if(back==1){
			msg.setCode("0000");
			msg.setMsg("删除成功");
			RedisDao.addUserUpdateList(user_id);
		}else{
			msg.setCode("1111");
			msg.setMsg("删除失败");
		}
		return msg;
	}
}
