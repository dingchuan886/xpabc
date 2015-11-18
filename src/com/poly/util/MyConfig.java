package com.poly.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MyConfig {
	public static String bbs_url;
	public static String bbs_img;
	public static String phone_reg;
	public static String img_savePath;
	public static String bbs_img_replace;
	public static String img_width_max;
	public static String userPhoto_defaut;
	public static String bbs_admin;
	
	public static String article_filter;
	
	static {
		Properties props = new Properties();

		try {
			InputStream in = MyConfig.class
					.getResourceAsStream("/myconfig.properties");
			props.load(in);
			in.close();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		bbs_url = props.getProperty("bbs_url");
		bbs_img = props.getProperty("bbs_img");
		phone_reg = props.getProperty("phone_reg");
		img_savePath = props.getProperty("img_savePath");
		bbs_img_replace = props.getProperty("bbs_img_replace");
		img_width_max = props.getProperty("img_width_max");
		userPhoto_defaut = props.getProperty("userPhoto_defaut");
		bbs_admin = props.getProperty("bbs_admin");
		try {
			article_filter = ArticleUtil.getFilterStr(props.getProperty("filter_filePath"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
