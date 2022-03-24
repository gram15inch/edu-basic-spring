package com.example.test2.learningtest.jdk.proxy;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Test;
import org.mockito.cglib.proxy.MethodProxy;
import org.springframework.aop.Advisor;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;
import org.springframework.util.PatternMatchUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class DynamicProxyText {

    @Test
    public void invokeMethod() throws Exception{
        String name = "Spring";

        // length()
        assertThat(name.length(),is(6));

        Method lengthMethod = String.class.getMethod("length");
        assertThat((Integer)lengthMethod.invoke(name),is(6));

        // charAt()
        assertThat(name.charAt(0), is('S'));

        Method charAtMethod = String.class.getMethod("charAt", int.class);
        assertThat((Character)charAtMethod.invoke(name,0),is('S'));

    }

    @Test
    public void simpleProxy(){
        Hello proxiedHello = (Hello)Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class[]{Hello.class},
                new UppercaseHandler(new HelloTarget())
        );

        assertThat(proxiedHello.sayHello("Toby"),is("HELLO TOBY"));
        assertThat(proxiedHello.sayHi("Toby"),is("HI TOBY"));
        assertThat(proxiedHello.sayThankYou("Toby"),is("THANK YOU TOBY"));
    }

    @Test
    public void proxyFactoryBean() throws Exception {

        ProxyFactoryBean pfBean = new ProxyFactoryBean();
        pfBean.setTarget(new HelloTarget());
        pfBean.addAdvice(new UppercaseAdvice());

        Hello proxiedHello = (Hello) pfBean.getObject();


        assertThat(proxiedHello.sayHello("Toby"),is("HELLO TOBY"));
        assertThat(proxiedHello.sayHi("Toby"),is("HI TOBY"));
        assertThat(proxiedHello.sayThankYou("Toby"),is("THANK YOU TOBY"));
    }

    @Test
    public  void pointcutAdvisor(){
        ProxyFactoryBean pfBean = new ProxyFactoryBean();
        pfBean.setTarget(new HelloTarget());

        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
        pointcut.setMappedName("sayH*");

        // 포인트컷 , 어드바이스 한번에 추가
        Advisor adv = new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice());
        pfBean.addAdvisor(adv);

        Hello proxiedHello = (Hello) pfBean.getObject();

        assertThat(proxiedHello.sayHello("Toby"),is("HELLO TOBY"));
        assertThat(proxiedHello.sayHi("Toby"),is("HI TOBY"));
        assertThat(proxiedHello.sayThankYou("Toby"),is("Thank You Toby"));

    }

    @Test
    public void classNamePointcutAdvisor(){
        NameMatchMethodPointcut classMethodPointcut = new NameMatchMethodPointcut(){
            @Override
            public ClassFilter getClassFilter() {
                return new ClassFilter() {
                    @Override
                    public boolean matches(Class<?> clazz) {
                        return clazz.getSimpleName().startsWith("HelloT") ;
                    }
                }  ;
            }
        };

        classMethodPointcut.setMappedName("sayH*");

        //테스트
        checkAdviced(new HelloTarget(), classMethodPointcut, true);

        class HelloWorld extends HelloTarget{};
        checkAdviced(new HelloWorld(), classMethodPointcut, false);

        class HelloToby extends HelloTarget{};
        checkAdviced(new HelloToby(), classMethodPointcut, true);
    }



    // 헬퍼 메소드
    private void checkAdviced(Object target, Pointcut pointcut, boolean adviced){
        ProxyFactoryBean pfBean = new ProxyFactoryBean();
        pfBean.setTarget(target);
        pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice()));
        Hello proxiedHello = (Hello) pfBean.getObject();

        if(adviced){
            assertThat(proxiedHello.sayHello("Toby"),is("HELLO TOBY"));
            assertThat(proxiedHello.sayHi("Toby"),is("HI TOBY"));
            assertThat(proxiedHello.sayThankYou("Toby"),is("Thank You Toby"));
        }else{
            assertThat(proxiedHello.sayHello("Toby"),is("Hello Toby"));
            assertThat(proxiedHello.sayHi("Toby"),is("Hi Toby"));
            assertThat(proxiedHello.sayThankYou("Toby"),is("Thank You Toby"));
        }
    }

    // 테스트용 클레스
    static class UppercaseAdvice implements MethodInterceptor {
        public Object invoke(MethodInvocation invocation) throws Throwable{
            String ret = (String)invocation.proceed();
            return ret.toUpperCase();
        }
    }
    static class HelloTarget implements Hello{

        @Override public String sayHello(String name) {
            return "Hello "+name;
        }
        @Override public String sayHi(String name) {
            return "Hi "+name;

        }
        @Override public String sayThankYou(String name) {
            return "Thank You "+name;

        }
    }

    // 테스트용 인터페이스
    interface Hello {
        String sayHello(String name);
        String sayHi(String name);
        String sayThankYou(String name);
    }
}


