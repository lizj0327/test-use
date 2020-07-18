package com.example.demo.post;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class TestUserAutoConfiguration {

    @Bean
    public MyBeanFactoryPostProcessor getMyBean(){
        MyBeanFactoryPostProcessor post = new MyBeanFactoryPostProcessor();
        return post;
    }
}
