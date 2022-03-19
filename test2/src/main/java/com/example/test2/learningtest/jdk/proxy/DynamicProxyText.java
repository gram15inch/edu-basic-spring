package com.example.test2.learningtest.jdk.proxy;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Test;
import org.mockito.cglib.proxy.MethodProxy;
import org.springframework.aop.framework.ProxyFactoryBean;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class DynamicProxyText {

    @Test
    public void simpleProxy(){
        Hello proxiedHello = (Hello)Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class[]{Hello.class},
                new UppercaseHandler(new HelloTarget())
        ) ;

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

    static class UppercaseAdvice implements MethodInterceptor {
        public Object invoke(MethodInvocation invocation) throws Throwable{
            String ret = (String)invocation.proceed();
            return ret.toUpperCase();
        }
    }

    interface Hello {
        String sayHello(String name);
        String sayHi(String name);
        String sayThankYou(String name);
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
}


