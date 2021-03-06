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
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
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

    @Value("${qiniu.key.access}")
    private String accessKey;

    @Value("${qiniu.key.secret}")
    private String secretKey;

    @Value("${qiniu.bucket.header.name}")
    private String headerBucketName;

    @Value("${qiniu.bucket.header.url}")
    private String headerBucketUrl;


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
    public String getSettingPage(Model model) {
        // ??????????????????
        String fileName = CommunityUtil.generateUUID();
        // ??????????????????
        StringMap policy = new StringMap();
        policy.put("returnBody", CommunityUtil.getJSONString(0));
        // ??????????????????
        Auth auth = Auth.create(accessKey, secretKey);
        String uploadToken = auth.uploadToken(headerBucketName, fileName, 3600, policy);

        model.addAttribute("uploadToken", uploadToken);
        model.addAttribute("fileName", fileName);

        return "/site/setting";
    }


    /**
     * ??????????????????
     * @param fileName
     * @return
     */
    @RequestMapping(path = "/header/url", method = RequestMethod.POST)
    @ResponseBody
    public String updateHeaderUrl(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            return CommunityUtil.getJSONString(1, "????????????????????????");
        }
        String url = headerBucketUrl + "/" + fileName;
        userService.updateHeader(hostHolder.getUser().getId(), url);
        return CommunityUtil.getJSONString(0);
    }


    /**
     * Deprecated
     */
    @Deprecated
    @LoginRequired
    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model) {
        if (headerImage == null) {
            model.addAttribute("error", "????????????????????????");
            return "site/setting";
        }
        String filename = headerImage.getOriginalFilename();
        // ???"."???????????????
        String suffix = filename.substring(filename.lastIndexOf("."));
        if (StringUtils.isBlank(suffix)) {
            model.addAttribute("error", "????????????????????????");
            return "site/setting";
        }

        // ????????????????????????
        filename = CommunityUtil.generateUUID() + suffix;


        // ???????????????????????????
        File dest = new File(uploadPath + "/" + filename);

        try {
            headerImage.transferTo(dest);
        } catch (Exception e) {
            logger.error("?????????????????????" + e.getMessage());
            throw new RuntimeException("?????????????????????????????????????????????");
        }

        // ????????????????????????????????????web???????????????
        // http://localhost:8080/community/user/header/xxx.png

        User user = hostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + filename;
        userService.updateHeader(user.getId(), headerUrl);

        return "redirect:/index";
    }


    /**
     * Deprecated
     */
    @Deprecated
    @RequestMapping(path = "/header/{fileName}", method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String filename, HttpServletResponse response) {
        // ?????????????????????
        filename = uploadPath + "/" + filename;
        // ???????????????
        String suffix = filename.substring(filename.lastIndexOf("."));
        // ????????????
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
            logger.error("??????????????????" + e.getMessage());
        }
    }

    @LoginRequired
    @RequestMapping(path = "/updatePassword", method = RequestMethod.POST)
    public String updateUserPassword(String oldPassword, String newPassword, String newPassword1, Model model, @CookieValue("ticket") String ticket) {
        // ?????????????????????????????????
        if (!newPassword.equals(newPassword1)) {
            model.addAttribute("newPasswordMsg", "??????????????????????????????");
            return "site/setting";
        }
        User user = hostHolder.getUser();
        if (!user.getPassword().equals(CommunityUtil.md5(oldPassword + user.getSalt()))) {
            model.addAttribute("passwordMsg", "???????????????????????????");
            return "site/setting";
        }
        userService.updatePassword(user.getId(), CommunityUtil.md5(newPassword + user.getSalt()));

        //???????????????????????????????????????
        userService.logout(ticket);
        return "redirect:/login";
    }


    @RequestMapping(path = "/forgetPassword", method = RequestMethod.POST)
    public String forgetUserPassword(String email, String verificationCode, String newPassword, Model model) {
        if (!userService.judgeVerificationCode(email, verificationCode)) {
            model.addAttribute("verificationCodeMsg", "????????????????????????");
            return "site/forget";
        }
        User user = userService.getUserIdFromEmail(email);
        userService.updatePassword(user.getId(), CommunityUtil.md5(newPassword + user.getSalt()));
        return "redirect:/login";
    }


    /**
     * ????????????
     *
     * @param userId
     * @param model
     * @return
     */
    @RequestMapping(path = "/profile/{userId}", method = RequestMethod.GET)
    public String getProfilePage(@PathVariable("userId") int userId, Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("??????????????????!");
        }
        model.addAttribute("user", user);
        // ????????????
        int count = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount", count);

        // ????????????
        long followeeCount = followService.findFolloweeCount(userId, ENTITY_USER);
        model.addAttribute("followeeCount", followeeCount);
        // ????????????
        long followerCount = followService.findFollowerCount(ENTITY_USER, userId);
        model.addAttribute("followerCount", followerCount);
        // ????????????????????????
        boolean hasFollowed = false;
        if (hostHolder.getUser() != null) {
            hasFollowed = followService.hasFollowed(hostHolder.getUser().getId(), ENTITY_USER, userId);
        }
        model.addAttribute("hasFollowed", hasFollowed);

        return "site/profile";
    }


}
