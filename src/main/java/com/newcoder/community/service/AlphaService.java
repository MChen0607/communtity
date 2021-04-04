package com.newcoder.community.service;

import com.newcoder.community.dao.AlphaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.sql.SQLOutput;

/*
* 单例，只被实例化 摧毁化一次
* */
@Service
//作用范围，默认为 singleton 一个 prototype 为多个实例
//@Scope("prototype")
public class AlphaService {
    @Autowired
    @Qualifier("AlphaDaoHibernate")
    private AlphaDao alphaDao;


    public AlphaService() {
        System.out.println("实例化AlphaService");
    }

    @PostConstruct
    private void init() {
        System.out.println("初始化AlphaService");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("销毁AlphaService");
    }

    public String find(){
        return alphaDao.select();
    }
}
