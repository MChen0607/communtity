package com.nowcoder.community.controller;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.FollowService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
//import netscape.security.PrivilegeTable;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@Controller
@RequestMapping("/user")
public class UserController implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    @Autowired
    private FollowService followService;

    @LoginRequired
    @RequestMapping(path = "/setting", method = RequestMethod.GET)
    public String getSettingPage() {
        return "/site/setting";
    }


    @LoginRequired
    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model) {
        if (headerImage == null) {
            model.addAttribute("error", "您还没有选择图片");
            return "site/setting";
        }
        String filename = headerImage.getOriginalFilename();
        // 从"."开始到结束
        String suffix = filename.substring(filename.lastIndexOf("."));
        if (StringUtils.isBlank(suffix)) {
            model.addAttribute("error", "文件的格式不正确");
            return "site/setting";
        }

        // 生成随机的文件名
        filename = CommunityUtil.generateUUID() + suffix;


        // 确定文件存放的路径
        File dest = new File(uploadPath + "/" + filename);

        try {
            headerImage.transferTo(dest);
        } catch (Exception e) {
            logger.error("上传文件失败：" + e.getMessage());
            throw new RuntimeException("上传文件失败，服务器发生异常！");
        }

        // 更新当前用户的头像路径（web访问路径）
        // http://localhost:8080/community/user/header/xxx.png

        User user = hostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + filename;
        userService.updateHeader(user.getId(), headerUrl);

        return "redirect:/index";
    }


    @RequestMapping(path = "/header/{fileName}", method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String filename, HttpServletResponse response) {
        // 服务器存放路径
        filename = uploadPath + "/" + filename;
        // 文件的后缀
        String suffix = filename.substring(filename.lastIndexOf("."));
        // 响应图片
        response.setContentType("image/" + suffix);
        try (
                OutputStream os = response.getOutputStream();
                FileInputStream fis = new FileInputStream(filename);
        ) {
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fis.read(buffer)) != -1) {
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败" + e.getMessage());
        }
    }

    @LoginRequired
    @RequestMapping(path = "/updatePassword", method = RequestMethod.POST)
    public String updateUserPassword(String oldPassword, String newPassword, String newPassword1, Model model, @CookieValue("ticket") String ticket) {
        // 这个应该由前端进行判断
        if (!newPassword.equals(newPassword1)) {
            model.addAttribute("newPasswordMsg", "两次输入的密码不一致");
            return "site/setting";
        }
        User user = hostHolder.getUser();
        if (!user.getPassword().equals(CommunityUtil.md5(oldPassword + user.getSalt()))) {
            model.addAttribute("passwordMsg", "您输入的密码不正确");
            return "site/setting";
        }
        userService.updatePassword(user.getId(), CommunityUtil.md5(newPassword + user.getSalt()));

        //修改成功后需要注销登陆凭证
        userService.logout(ticket);
        return "redirect:/login";
    }


    @RequestMapping(path = "/forgetPassword", method = RequestMethod.POST)
    public String forgetUserPassword(String email,String verificationCode, String newPassword,Model model) {
        if (!userService.judgeVerificationCode(email, verificationCode)) {
            model.addAttribute("verificationCodeMsg", "验证码输入不正确");
            return "site/forget";
        }
        User user = userService.getUserIdFromEmail(email);
        userService.updatePassword(user.getId(), CommunityUtil.md5(newPassword + user.getSalt()));
        return "redirect:/login";
    }


    /**
     * 个人主页
     *
     * @param userId
     * @param model
     * @return
     */
    @RequestMapping(path = "/profile/{userId}", method = RequestMethod.GET)
    public String getProfilePage(@PathVariable("userId") int userId, Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在!");
        }
        model.addAttribute("user", user);
        // 点赞数量
        int count = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount", count);

        // 关注数量
        long followeeCount = followService.findFolloweeCount(userId, ENTITY_USER);
        model.addAttribute("followeeCount", followeeCount);
        // 粉丝数量
        long followerCount = followService.findFollowerCount(ENTITY_USER, userId);
        model.addAttribute("followerCount", followerCount);
        // 当前用户关注情况
        boolean hasFollowed = false;
        if (hostHolder.getUser() != null) {
            hasFollowed = followService.hasFollowed(hostHolder.getUser().getId(), ENTITY_USER, userId);
        }
        model.addAttribute("hasFollowed", hasFollowed);

        return "site/profile";
    }


}
