package com.newcoder.community.util;

public interface CommunityConstant {
    /**
     * 激活成功
     */
    int ACTIVATION_SUCCESS = 0;
    /**
     * 重复激活
     */
    int ACTIVATION_REPEAT = 1;
    /**
     * 激活失败
     */
    int ACTIVATION_FAILURE = 2;
    /**
     * 默认状态的登录凭证的超时时间
     */
    int DEFAULT_EXPIRED_SECONDS = 3600 * 12;//0.5天

    /**
     * 记录状态的登录凭证超时时间
     */
    int REMEMBER_EXPIRED_SECONDS = 3600 * 24 * 100;//100天

    /**
     * 实体类型：帖子
     */
    int ENTITY_POST = 1;

    /**
     * 实体类型：评论
     */
    int ENTITY_COMMENT = 2;

    /**
     * 实体类型：用户
     */
    int ENTITY_USER = 3;

    /**
     * 主题：评论
     */
    String TOPIC_COMMENT = "comment";

    /**
     * 主题：评论
     */
    String TOPIC_LIKE = "like";

    /**
     * 主题：评论
     */
    String TOPIC_FOLLOW = "follow";

    /**
     * 发帖
     */
    String TOPIC_PUBLISH = "publish";

    /**
     * 系统用户
     */
    int SYSTEM_USER_ID = 1;

}

