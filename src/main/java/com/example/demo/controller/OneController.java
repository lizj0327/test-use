package com.example.demo.controller;

import com.example.demo.post.MyBeanPostProcessor;
import nonInject.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OneController {

    @Autowired
    MyBeanPostProcessor myBeanPostProcessor;
//    @Autowired
//    AutowiredAnnotationBeanPostProcessor AutowiredAnnotationBeanPostProcessor;
    @Autowired
    ApplicationContext ApplicationContext;

    @Autowired
    Dgfd dgfd;
    @Autowired
    Test1 test1;
    @Autowired
    @Qualifier("test22")
    Test2 test2;
    @Autowired
    Test3 test3;
    @Autowired
    Gfdsg gfdsg;

    public void setMyBeanPostProcessor(MyBeanPostProcessor myBeanPostProcessor) {
        this.myBeanPostProcessor = myBeanPostProcessor;
    }

    @GetMapping("/fds")
    public void test1(){
        System.out.println(myBeanPostProcessor);
//        System.out.println(AutowiredAnnotationBeanPostProcessor);
        System.out.println(ApplicationContext);
        System.out.println(dgfd);
        System.out.println(test1);
        System.out.println(test2);
        System.out.println(test3);
        System.out.println("fdsfs");
    }
}
