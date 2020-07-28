package com.example.demo.post;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public class MyBeanFactoryPostProcessor extends AutowiredAnnotationBeanPostProcessor implements BeanFactoryPostProcessor, ApplicationContextAware, InitializingBean {

    DefaultListableBeanFactory defaultListableBeanFactory;

    private ApplicationContext applicationContext;

    private final Set<Class<? extends Annotation>> autowiredAnnotationTypes = new LinkedHashSet(4);

    private List<Class> initElements = new ArrayList<>();



    public MyBeanFactoryPostProcessor() {
        this.autowiredAnnotationTypes.add(Autowired.class);
        this.autowiredAnnotationTypes.add(Value.class);
//
//        try {
//            this.autowiredAnnotationTypes.add(ClassUtils.forName("javax.inject.Inject", MyBeanFactoryPostProcessor.class.getClassLoader()));
//        } catch (ClassNotFoundException var2) {
//        }

    }
    public void setInitElements(List<String> classNames) throws ClassNotFoundException {
        for(String className:classNames){
            initElements.add(ClassUtils.forName(className,MyBeanFactoryPostProcessor.class.getClassLoader()));
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        MyBeanFactoryPostProcessor a = applicationContext.getBean(MyBeanFactoryPostProcessor.class);
        System.out.println(a);
        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) configurableListableBeanFactory;
        this.defaultListableBeanFactory = defaultListableBeanFactory;
        for(Class element:initElements){
            invoke1(element);
        }

//        invoke1(Dgfd.class);
//        Object bean = applicationContext.getBean(Dgfd.class);
//        System.out.println(bean);
    }
    private void invoke1(Class clazz){
        List<AutowiredFieldElement> clazzs = new ArrayList<>();
        getChildren(clazz,clazzs);
        AutowiredFieldElement autowiredFieldElement = new AutowiredFieldElement(clazz,true);
        clazzs.add(autowiredFieldElement);
        for(AutowiredFieldElement clazz1 : clazzs){
            setNewBean(clazz1);
        }
    }

    private void getChildren(Class clazz,List<AutowiredFieldElement> clazzs){
        List<AutowiredFieldElement> metadata = findAutowiringMetadata(clazz.getSimpleName(), clazz);
        for(AutowiredFieldElement m:metadata){
            Field field = m.getField();
            Class clazz1 = field.getType();
            boolean bean = applicationContext.containsBeanDefinition(m.getName());
            if(!bean){
                clazzs.add(m);
            }
            getChildren(clazz1,clazzs);
        }
    }

    private void setNewBean(AutowiredFieldElement element){
        boolean bean = applicationContext.containsBeanDefinition(element.getName());
        if(element.getClazz().isInterface()){
            return;
        }
        if(!bean){
            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(element.getClazz());
            defaultListableBeanFactory.registerBeanDefinition(element.getName(), beanDefinitionBuilder.getRawBeanDefinition());
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private List<AutowiredFieldElement> findAutowiringMetadata(String beanName, Class<?> clazz) {
        String cacheKey = StringUtils.hasLength(beanName) ? beanName : clazz.getName();
        List<AutowiredFieldElement> metadata = this.buildAutowiringMetadata(clazz);

        return metadata;
    }

    private List<AutowiredFieldElement> buildAutowiringMetadata(Class<?> clazz) {
        if (!AnnotationUtils.isCandidateClass(clazz, this.autowiredAnnotationTypes)) {
            return null;
        } else {
            List<AutowiredFieldElement> elements = new ArrayList();
            Class targetClass = clazz;

            do {
                List<AutowiredFieldElement> currElements = new ArrayList();
                ReflectionUtils.doWithLocalFields(targetClass, (field) -> {
                    MergedAnnotation<?> ann = this.findAutowiredAnnotation(field);
                    if (ann != null) {
                        if (Modifier.isStatic(field.getModifiers())) {
                            if (this.logger.isInfoEnabled()) {
                                this.logger.info("Autowired annotation is not supported on static fields: " + field);
                            }

                            return;
                        }

                        boolean required = this.determineRequiredStatus(ann);
                        currElements.add(new MyBeanFactoryPostProcessor.AutowiredFieldElement(field, required));
                    }

                });
                elements.addAll(0, currElements);
                targetClass = targetClass.getSuperclass();
            } while(targetClass != null && targetClass != Object.class);

//            return InjectionMetadata.forElements(elements, clazz);
            return elements;
        }
    }

    private MergedAnnotation<?> findAutowiredAnnotation(AccessibleObject ao) {
        MergedAnnotations annotations = MergedAnnotations.from(ao);
        Iterator var3 = this.autowiredAnnotationTypes.iterator();

        MergedAnnotation annotation;
        do {
            if (!var3.hasNext()) {
                return null;
            }

            Class<? extends Annotation> type = (Class)var3.next();
            annotation = annotations.get(type);
        } while(!annotation.isPresent());

        return annotation;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        String classes = applicationContext.getEnvironment().getProperty("test.init.classes");
        if(!StringUtils.isEmpty(classes)){
            List<String> classNames = new ArrayList<>();
            Collections.addAll(classNames,classes.split(","));
            setInitElements(classNames);
        }

    }

    private class AutowiredFieldElement{
        private final boolean required;
        private Field field;
        private String name;
        private Class clazz;

        public AutowiredFieldElement(Field field, boolean required) {
            this.required = required;
            this.field = field;
            this.clazz = field.getType();
            String name = field.getType().getSimpleName();
            Qualifier annotation = field.getAnnotation(Qualifier.class);
            if(annotation!=null && !StringUtils.isEmpty(annotation.value())){
                name = annotation.value();
            }
            this.name = firstLower(name);
        }
        public AutowiredFieldElement(Class clazz, boolean required) {
            this.required = required;
            this.clazz = clazz;
            String name = clazz.getSimpleName();
            Service annotation = (Service) clazz.getAnnotation(Service.class);
            if(annotation!=null && !StringUtils.isEmpty(annotation.value())){
                name = annotation.value();
            }
            Component annotation1 = (Component) clazz.getAnnotation(Component.class);
            if(annotation1!=null && !StringUtils.isEmpty(annotation1.value())){
                name = annotation1.value();
            }
            this.name = firstLower(name);
        }

        public Field getField(){
            return field;
        }

        public String getName() {
            return name;
        }

        public Class getClazz(){
            return clazz;
        }
        private String firstLower(String name){
            return name.substring(0,1).toLowerCase()+name.substring(1);
        }

    }

}
