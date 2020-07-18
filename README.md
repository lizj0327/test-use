# test-use-spring-boot-starter
在很多项目中，自定义的bean特别多，启动很慢要十多分钟，可以去掉自动加载这些bean，然后引用这个jar，配置就可以。  
根据application.properties中的test.init.classes属性配置了多个入口类之后，入口类和它需要自动注入的属性对应的类都会以BeanDefinition注册到spring的factory中，  
而程序会递归解析各个类需要注入的属性以保障需要的类都注册到factory中，在spring实例化和注入属性时就可以找到对应的类。  
核心是MyBeanFactoryPostProcessor类，其他的是辅助测试的类。  
1、引入这个jar  
2、在application.properties中配置test.init.classes属性，添加一个或者多个类，以，隔开例如test.init.classes=com.aa,com.bb
使用方法：
###<dependency>  
    <groupId>com.example</groupId>  
    <artifactId>test-use-spring-boot-starter</artifactId>  
    <version>0.0.1-SNAPSHOT</version>  
</dependency>  
