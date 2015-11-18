package com.poly.redis;

import java.io.IOException;
import java.util.Properties;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import com.poly.servlet.InitServlet;

public class RedisManager {

	private static JedisPool pool;
	
	private static int preTomIdx = 0;
	
	private static int timeout=2000;


	static {

		try {

			Properties props = new Properties();
			props.load(RedisManager.class.getClassLoader().getResourceAsStream("redis.properties"));

			JedisPoolConfig config = new JedisPoolConfig();
			config.setMinIdle(Integer.valueOf(props.getProperty("jedis.pool.minIdle")));
			config.setMaxIdle(Integer.valueOf(props.getProperty("jedis.pool.maxIdle")));
			config.setMaxWaitMillis(Long.valueOf(props.getProperty("jedis.pool.maxWaitMillis")));
			config.setTestOnBorrow(Boolean.valueOf(props.getProperty("jedis.pool.testOnBorrow")));
			config.setTestOnReturn(Boolean.valueOf(props.getProperty("jedis.pool.testOnReturn")));
			config.setMaxTotal(Integer.valueOf(props.getProperty("jedis.pool.maxActive")));

			preTomIdx = Integer.valueOf(props.getProperty("prepare.tomcat.index"));
			
			timeout = Integer.valueOf(props.getProperty("jedis.pool.timeout"));

			pool = new JedisPool(config, props.getProperty("redis.ip"),
					Integer.valueOf(props.getProperty("redis.port")), timeout);
			
			System.out.println(">>> >>> redis is ready");

		} catch (IOException e) {

			e.printStackTrace();

		}

	}


	public static Jedis getJedisObject() {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
		} catch (Exception e) {
			if (jedis != null)
				pool.returnBrokenResource(jedis);
			jedis = pool.getResource();
			e.printStackTrace();
		}
		return jedis;
	}

	public static void recycleJedisOjbect(Jedis jedis) {
		pool.returnResource(jedis);
	}
	
	public static long getIdx(String key) {
		Long idx = -1l;
		Jedis jedis = getJedisObject();// ���jedisʵ��
		idx = jedis.incr(key);
		System.out.println("idx:" + idx);
		recycleJedisOjbect(jedis);
		return idx;
	}

	public static long getIdx() {
		String idxStr = "";
		int tom = preTomIdx;
		if (InitServlet.rootPath != null) {
			int begin = InitServlet.rootPath.indexOf("/Tomcat");
			if (begin > 0 && InitServlet.rootPath.length() >= begin + 8) {
				String tomFlag = InitServlet.rootPath.substring(begin + 7, begin + 8);
				tom = Integer.valueOf(tomFlag);
			}
		}
		idxStr = String.valueOf(tom);
		System.out.println("idxStr-----1--->" + idxStr);
		idxStr = idxStr + String.valueOf(System.currentTimeMillis());
		System.out.println("idxStr-----2--->" + idxStr);
		idxStr = idxStr + String.valueOf((int) (10 * Math.random()));
		System.out.println("idxStr-----3--->" + idxStr);
		return Long.valueOf(idxStr);
	}
}
