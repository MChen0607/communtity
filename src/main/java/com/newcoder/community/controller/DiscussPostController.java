package com.newcoder.community.controller;

import com.newcoder.community.dao.CommentMapper;
import com.newcoder.community.dao.DiscussPostMapper;
import com.newcoder.community.entity.Comment;
import com.newcoder.community.entity.DiscussPost;
import com.newcoder.community.entity.Page;
import com.newcoder.community.entity.User;
import com.newcoder.community.service.CommentService;
import com.newcoder.community.service.DiscussPostService;
import com.newcoder.community.service.UserService;
import com.newcoder.community.util.CommunityConstant;
import com.newcoder.community.util.CommunityUtil;
import com.newcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {
    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title, String content) {
        User user = hostHolder.getUser();
        if (user == null) {
            return CommunityUtil.getJSONString(403, "你还没有登录");
        }
        DiscussPost discussPost = new DiscussPost();
        discussPost.setUserId(user.getId());
        discussPost.setTitle(title);
        discussPost.setContent(content);
        discussPost.setCreateTime(new Date());
        //以下内容的默认值为0，可不写
        //        discussPost.setCommentCount(0);
        //        discussPost.setScore(0.0);
        //        discussPost.setStatus(0);
        //        discussPost.setType(0);
        discussPostService.addDiscussPost(discussPost);

        //报错的情况，将来统一处理
        return CommunityUtil.getJSONString(0, "发布成功!");
    }

    @RequestMapping(path = "/detail/{discussPostId}", method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model, Page page) {
        // 帖子
        DiscussPost discussPost = discussPostService.findDiscussPostById(discussPostId);
        model.addAttribute("discussPost", discussPost);
        //帖子作者
        User user = userService.findUserById(discussPost.getUserId());
        model.addAttribute("user", user);

        //查评论的分页信息
        page.setLimit(5);
        page.setPath("/discuss/detail/" + discussPostId);
        page.setRows(discussPost.getCommentCount());//从帖子中取评论的数目

        // 评论：给帖子评论
        // 回复：给评论回复
        // 评论列表
        List<Comment> commentList = commentService.findCommentsByEntity(
                ENTITY_POST, discussPost.getId(), page.getOffset(), page.getLimit());

        // 评论VO表
        List<Map<String, Object>> commentVOList = new ArrayList<>();

        if (commentList != null) {
            for (Comment comment : commentList) {
                // 评论VO
                Map<String, Object> commentVO = new HashMap<>();
                // 评论
                commentVO.put("comment", comment);
                // 作者
                commentVO.put("user", userService.findUserById(comment.getUserId()));

                // 回复列表
                List<Map<String, Object>> replyVOList = new ArrayList<>();
                List<Comment> replyList = commentService.findCommentsByEntity(ENTITY_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);
                if (replyList != null) {
                    for (Comment reply : replyList) {
                        Map<String, Object> replayVO = new HashMap<>();
                        // 回复
                        replayVO.put("reply", reply);
                        // 作者
                        replayVO.put("user", userService.findUserById(reply.getUserId()));
                        // 回复的目标
                        User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                        replayVO.put("target", target);
                        replyVOList.add(replayVO);
                    }
                }
                commentVO.put("replies", replyVOList);

                // 回复数量
                int replyCount = commentService.findCommentsCount(ENTITY_COMMENT, comment.getId());
                commentVO.put("replyCount", replyCount);
                commentVOList.add(commentVO);
            }
        }
        model.addAttribute("comments",commentVOList);
        return "/site/discuss-detail";

    }


}
