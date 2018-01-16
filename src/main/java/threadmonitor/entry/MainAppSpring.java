/*
 * Copyright (c) 2018年01月10日 by XuanWu Wireless Technology Co.Ltd. 
 *             All rights reserved                         
 */
package threadmonitor.entry;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @Description
 * @Author <a href="mailto:haosonglin@wxchina.com">songlin.Hao</a>
 * @Date 2018/1/10
 * @Version 1.0.0
 */
public class MainAppSpring {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-config.xml");
        Hello obj = (Hello) context.getBean("helloWorld");
        obj.getMessage();
    }
}
