package com.newcoder.community;

import com.newcoder.community.util.MailClinet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MailTest {

    @Autowired
    private MailClinet mailClinet;

    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void testToEmail(){
        mailClinet.sendMail("51205902152@stu.ecnu.edu.cn","TEST","test,hello.");
//        mailClinet.sendMail("1141981183@qq.com","无主题？？？","test,hello.");
    }

    @Test
    public void tesHtmlMail(){
        Context context=new Context();
        context.setVariable("username","sunday");
        String content=templateEngine.process("/mail/demo",context);
        System.out.println(content);

        mailClinet.sendMail("51205902152@stu.ecnu.edu.cn","HTML",content);
    }


}
