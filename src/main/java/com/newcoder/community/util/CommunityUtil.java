package com.newcoder.community.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.UUID;

public class CommunityUtil {
    //生成随机字符串
    public static String generateUUID() {
        return UUID.randomUUID().toString().replaceAll(" ", "");
    }

    //MD5加密
    //添加一个随机字符串让加密更加难以破解
    //hello -> abc123def456
    //hello + 3e4a8->abc123abc123abc456
    public static String md5(String key) {
        if (StringUtils.isBlank(key)) {//判断 空
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());//变成16进制
    }
}
