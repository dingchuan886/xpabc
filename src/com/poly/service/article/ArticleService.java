package com.poly.service.article;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;
import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;

import com.poly.redis.RedisManager;
import com.poly.service.BaseService;
import com.poly.util.LuceneUtil;
import com.poly.util.ObjectToMapUtil;

@Service("articleService")
public class ArticleService extends BaseService {
	
	public void persistentArticle() throws ParseException {
		Jedis jedis = RedisManager.getJedisObject();
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			
			Set<String> set_up = jedis.zrangeByScore("tb_article_update", "-inf", "+inf");
			Set<String> set_in = jedis.zrangeByScore("tb_article_insert", "-inf", "+inf");
//			Set<String> set_del = jedis.zrangeByScore("tb_article_delete", "-inf", "+inf");
			BASE64Encoder encoder = new BASE64Encoder();
			int[] types = {java.sql.Types.INTEGER, java.sql.Types.VARCHAR, java.sql.Types.TIME};
			
			//插入文章
			for (String id : set_in) {
				Map<String, String> map = jedis.hgetAll("tb_article:article_id:" + id);
				double score = 0;
				Double ss = jedis.zscore("tb_article:list:look", id + "");
				if (ss!=null) score = ss;
				String sql = "insert into tb_article(`article_id`,`user_id`,`article_title`,`article_content`,`plate_id`,"
													+ "`add_time`,`update_time`,`article_state`,"
													+ "`is_delete`,`article_lookcount`,`is_elite`,`article_order`) "
											+ "values("+ id +","+ map.get("user_id") +",?,?,"+ map.get("plate_id") +","
													+ "'"+ map.get("add_time") +"','"+ map.get("update_time") +"',"+ map.get("article_state") +","
													+ ""+ map.get("is_delete") +","+ score +","+ map.get("is_elite") +","+ map.get("article_order") +")";
				String content = encoder.encode(map.get("article_content").getBytes());
				jdbc.update(sql, new Object[]{map.get("article_title"),content}, new int [] {types[1],types[1]});
				jedis.zrem("tb_article_insert", id);
			}
			
			//更新文章
			for (String id : set_up) {
				Map<String, String> map = jedis.hgetAll("tb_article:article_id:" + id);
				double score = 0;
				Double ss = jedis.zscore("tb_article:list:look", id + "");
				if (ss!=null) score = ss;
				String sql = "update tb_article set article_content=?, "
												+ "plate_id="+ map.get("plate_id") +", "
												+ "update_time='"+ map.get("update_time") +"', "
												+ "article_state="+ map.get("article_state") +", "
												+ "is_delete="+ map.get("is_delete") +", "
												+ "article_lookcount="+ score +", "
												+ "is_elite="+ map.get("is_elite") +", "
												+ "article_order="+ map.get("article_order") +" "
												+ "where article_id=" + id;
				String content = encoder.encode(map.get("article_content").getBytes());
				jdbc.update(sql, new Object[]{content}, new int[]{types[1]});
				jedis.zrem("tb_article_update", id);
			}
			
		} finally {
			RedisManager.recycleJedisOjbect(jedis);
		}
	}
	
	public void toRedis() {
		String sql = "SELECT t1.*,t2.is_in FROM tb_article t1 LEFT JOIN tb_plate t2 ON t1.plate_id=t2.plate_id ORDER BY article_id";
		List<Map<String, Object>> list = jdbc.queryForList(sql);
		if (list!=null) {
			BASE64Decoder decoder = new BASE64Decoder();
			Jedis jedis = RedisManager.getJedisObject();
			int incr = 0;
			try {
				for (int i = 0; i < list.size(); i++) {
					Map<String, Object> map = list.get(i);
					int article_id = (int) map.get("article_id");
					incr = article_id;
					int is_delete = (int) map.get("is_delete");
					if (is_delete==0) {
						int is_in = (int) map.get("is_in");
						
						map.remove("is_in");
						Map<String, String> map2 = ObjectToMapUtil.changeToMap(map);
						try {
							String content = new String(decoder.decodeBuffer(map2.get("article_content")));
							map2.put("article_content", content);
							Date date = (Date) map.get("add_time");
							long timer = date.getTime();
							jedis.hmset("tb_article:article_id:" + article_id, map2);
							jedis.zadd("tb_article:list:plate_id:" + map2.get("plate_id"), timer, article_id + "");
							jedis.zadd("tb_article:list:user_id:" + map2.get("user_id"), timer, article_id + "");
							if (map.get("article_state")!=null) {
								int state = (int) map.get("article_state");
								jedis.zadd("tb_article:list:article_state:" + state, timer, article_id + "");
							}
							
							if (is_in==0) {
								jedis.zadd("tb_article:list:all", timer, article_id + "");
							} else {
								jedis.zadd("tb_article:list:allin", timer, article_id + "");
							}
							
							//浏览量
							int lookCount = (int) map.get("article_lookcount");
							jedis.zadd("tb_article:list:look", lookCount, article_id + "");
							
							//置顶
							long article_order = (long) map.get("article_order");
							jedis.zadd("tb_article:list:article_order", article_order, article_id + "");
							
							//精华帖子
							int is_elite = (int) map.get("is_elite");
							if (is_elite == 1) {
								jedis.zadd("tb_article:list:is_elite:1", timer, article_id + "");
							}
							
					    	//创建搜索
					    	Map<String, String> searchMap = new HashMap<String, String>();
					    	searchMap.put("title", map2.get("article_title"));
					    	searchMap.put("userid", map2.get("user_id"));
					    	searchMap.put("plateid", map2.get("plate_id"));
					    	searchMap.put("id", map2.get("article_id"));
					    	searchMap.put("addtime", timer + "");
					    	try {
					    		LuceneUtil.insertorUpdateIndex(searchMap, 2,1);//全部帖子
					    		if(is_in==0){
					    			LuceneUtil.insertorUpdateIndex(searchMap, 2,2);//外部帖子
					    		}
							} catch (IOException e) {
								e.printStackTrace();
							}
							
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					
				}
				
				if (jedis.exists("tb_article_articleid_incr")) {
					String inc = jedis.get("tb_article_articleid_incr");
					try {
						int ii = Integer.parseInt(inc);
						if (incr<ii) {
							incr = ii;
						}
					} catch (Exception e) {
					}
				}
				
				jedis.set("tb_article_articleid_incr", incr + "");  //创建自增主键
			}  finally {
				RedisManager.recycleJedisOjbect(jedis);
			}
		}
	}
}
