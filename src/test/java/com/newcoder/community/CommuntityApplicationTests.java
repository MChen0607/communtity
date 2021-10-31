package com.newcoder.community;

import com.newcoder.community.dao.AlphaDao;
import com.newcoder.community.service.AlphaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;

import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
class CommuntityApplicationTests implements ApplicationContextAware {

//	@Test
//	void contextLoads() {
//		System.out.println("Test");
//	}


    private ApplicationContext applicationContext;

    /*
     * ApplicationContext 就是Spring容器  管理bean
     * */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Test
    public void testApplicationContext() {
        System.out.println(applicationContext);
        AlphaDao alphaDao = applicationContext.getBean(AlphaDao.class);//获取bean
        System.out.println(alphaDao.select());

        AlphaDao alphaDao1 = applicationContext.getBean("AlphaDaoHibernate", AlphaDao.class);
        System.out.println(alphaDao1.select());
    }

    @Test
    public void testBeanManagement() {
        AlphaService alphaService = applicationContext.getBean(AlphaService.class);
        System.out.println(alphaService);
        AlphaService alphaService1=applicationContext.getBean(AlphaService.class);
        System.out.println(alphaService1);
    }

    @Test
    public void testBeanConfig() {
        SimpleDateFormat simpleDateFormat = applicationContext.getBean(SimpleDateFormat.class);
        System.out.println(simpleDateFormat.format(new Date()));

    }

    /*
    * 依赖注入
    * 可以加到：属性，类的构造器，get set方法。具体见手册。
    * controller 处理浏览器请求，会调用业务组件来处理当前业务，业务组件会调用dao来处理有关数据库的内容。
    * */
    @Autowired
    @Qualifier("AlphaDaoHibernate")
    private AlphaDao alphaDao;

    @Autowired
    private AlphaService alphaService;
    @Autowired
    private SimpleDateFormat simpleDateFormat;
    @Test
    public void testDI(){
        System.out.println(alphaDao.select());
        System.out.println(simpleDateFormat.format(new Date()));
        System.out.println(alphaService);
    }
}
