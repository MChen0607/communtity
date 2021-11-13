package com.nowcoder.community.service;

import com.nowcoder.community.dao.AlphaDao;
import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Date;

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

    private static final Logger logger = LoggerFactory.getLogger(AlphaService.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;


    @Autowired
    private TransactionTemplate transactionTemplate;

    public AlphaService() {
//        System.out.println("实例化AlphaService");
    }

    @PostConstruct
    private void init() {
//        System.out.println("初始化AlphaService");
    }

    @PreDestroy
    public void destroy() {
//        System.out.println("销毁AlphaService");
    }

    public String find() {
        return alphaDao.select();
    }


    // REQUIRED : 支持当前事务（外部事务），如果不存在则创建新事务.
    // REQUIRES_NEW ： 创建一个新事务，并且暂停当前事务（外部事务）.
    // NEVER: 如果当前存在事务（外部事务），则嵌套在该事务中执行（独立的提交和回滚），否则就会和REQUIRED一样.
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public Object save1() {
        //新增用户
        User user = new User();
        user.setUsername("alpha");
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword(CommunityUtil.md5("123" + user.getSalt()));
        user.setEmail("alpha@qq.com");
        user.setHeaderUrl("http://images.nowcoder.com/head/5t.png");
        user.setCreateTime(new Date());
        userMapper.insertUser(user);
        //新增评论
        DiscussPost post = new DiscussPost();
        post.setTitle("Hello");
        post.setContent("新人报道");
        post.setCreateTime(new Date());
        discussPostMapper.insertDiscussPost(post);

        Integer.valueOf("abc");

        return "Ok";
    }

    public Object save2() {
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        return transactionTemplate.execute(
                new TransactionCallback<Object>() {
                    @Override
                    public Object doInTransaction(TransactionStatus transactionStatus) {
                        //新增用户
                        User user = new User();
                        user.setUsername("beafsdfa");
                        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
                        user.setPassword(CommunityUtil.md5("123" + user.getSalt()));
                        user.setEmail("asdfas@qq.com");
                        user.setHeaderUrl("http://images.nowcoder.com/head/10t.png");
                        user.setCreateTime(new Date());
                        userMapper.insertUser(user);
                        //新增评论
                        DiscussPost post = new DiscussPost();
                        post.setTitle("asdfasdfasdf");
                        post.setContent("asdfasdfafdas");
                        post.setCreateTime(new Date());
                        discussPostMapper.insertDiscussPost(post);

                        Integer.valueOf("abc");

                        return "Ok";
                    }
                }
        );
    }

    /**
     * 让该方法在多线程环境下,被异步调用
     */
    @Async
    public void execute1(){
        logger.debug("execute1");
    }

//    @Scheduled(initialDelay = 10000,fixedDelay = 1000)
    public void execute2(){
        logger.debug("execute2");
    }

}
