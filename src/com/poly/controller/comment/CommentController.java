package com.poly.controller.comment;

import java.util.Date;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.poly.bean.TbComment;
import com.poly.bean.User;
import com.poly.redis.RedisDao;
import com.poly.util.MyConfig;

@Controller
@RequestMapping("/comment")
public class CommentController {
	
	@RequestMapping("/add")
	@ResponseBody
	public String add(int articleId, int comId, String content, HttpSession session) {
		User user = (User) session.getAttribute("user");
		if (user==null) {
			return "-1";
		}else {
			content = content.replaceAll(MyConfig.article_filter, "");
			if (content==null) {
				return "0";
			}
			TbComment tbComment = new TbComment();
			tbComment.setAdd_time(new Date());
			tbComment.setArticle_id(articleId);
			tbComment.setComment_id(comId);
			tbComment.setUser_id(user.getUser_id());
			tbComment.setCom_content(content);
			
			boolean result = RedisDao.comment_add(tbComment);
			if (result) {
				return "1";
			}else {
				return "0";
			}
		}
	}
}
