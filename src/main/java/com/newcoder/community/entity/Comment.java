package com.newcoder.community.entity;

import lombok.Data;

import java.util.Date;


@Data
public class Comment {
    private int id;
    private int userId;
    private int entityType;
    private int entityId;
    private int targetId;
    private String content;
    private Date createTime;

}
