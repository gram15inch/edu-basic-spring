package com.example.test2.learningtest.pointcut;

import com.example.test2.domain.Level;
import com.example.test2.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class pointcutTest {

    @Test
    public void showTargetPath() throws NoSuchMethodException{
        System.out.println(Target.class.getMethod("minus", int.class, int.class));
    }

    @Test
    public void methodSignaturePointcut() throws SecurityException,NoSuchMethodException{
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("execution(public int " +
                "com.example.test2.learningtest.pointcut.Target.minus(int,int) " +
                "throws java.lang.RuntimeException)");

        // Target.minus()
        assertThat(pointcut.getClassFilter().matches(Target.class) &&
                pointcut.getMethodMatcher().matches(
                        Target.class.getMethod("minus", int.class, int.class), null),is(true));

        // Target.plus()
        assertThat(pointcut.getClassFilter().matches(Target.class) &&
                pointcut.getMethodMatcher().matches(
                        Target.class.getMethod("plus", int.class, int.class),null),is(false));

        // Bean.method()
        assertThat(pointcut.getClassFilter().matches(Bean.class)&&
                pointcut.getMethodMatcher().matches(
                        Target.class.getMethod("method"),null),is(false));

    }

    @Test
    public void pointcut() throws  Exception {                        // h() h(S) p(i,i) mi(i,i) T.me() B.me()
        targetClassPointcutMatches("execution(* *(..))",true,true,true,true,true,true); // 1
        targetClassPointcutMatches("execution(* hello())",true,false,false,false,false,false); // 2
        targetClassPointcutMatches("execution(* hello(String))",false,true,false,false,false,false); // 3
        targetClassPointcutMatches("execution(* meth*(..))",false,false,false,false,true,true); // 4
        targetClassPointcutMatches("execution(* *(int,int))",false,false,true,true,false,false); // 5
        targetClassPointcutMatches("execution(* *())",true,false,false,false,true,true); // 6
        targetClassPointcutMatches("execution(* com.example.test2.learningtest.pointcut.Target.*(..))"
                                                                    ,true,true,true,true,true,false); // 7
        targetClassPointcutMatches("execution(* com.example.test2.learningtest.pointcut.*.*(..))"
                                                                    ,true,true,true,true,true,true); // 8
        targetClassPointcutMatches("execution(* com.example.test2.learningtest.pointcut..*.*(..))"
                                                                    ,true,true,true,true,true,true); // 9
        targetClassPointcutMatches("execution(* com.example.test2..*.*(..))"
                                                                    ,true,true,true,true,true,true); // 10

        targetClassPointcutMatches("execution(* com..*.*(..))",true,true,true,true,true,true); // 11 교재는 all false ???
        targetClassPointcutMatches("execution(* *..Target.*(..))",true,true,true,true,true,false); // 12
        targetClassPointcutMatches("execution(* *..Tar*.*(..))",true,true,true,true,true,false); // 13
        targetClassPointcutMatches("execution(* *..*get.*(..))",true,true,true,true,true,false); // 14
        targetClassPointcutMatches("execution(* *..B*.*(..))",false,false,false,false,false,true); // 15
        targetClassPointcutMatches("execution(* *..TargetInterface.*(..))",true,true,true,true,false,false); // 16
        targetClassPointcutMatches("execution(* *(..) throws Runtime*)",false,false,false,true,false,true); // 17
        targetClassPointcutMatches("execution(int *(..))",false,false,true,true,false,false); // 18
        targetClassPointcutMatches("execution(void *(..))",true,true,false,false,true,true); // 19
    }

    // 헬퍼 메소드
    public void pointcutMatches(String expression, Boolean expected, Class<?> clazz, String methodName, Class<?>... args)
            throws Exception{
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression(expression);

        assertThat(pointcut.getClassFilter().matches(clazz) &&
                    pointcut.getMethodMatcher().matches(clazz.getMethod(methodName,args),null),
                is(expected));
    }

    public void targetClassPointcutMatches(String expression, boolean... expected) throws Exception{
        pointcutMatches(expression, expected[0],Target.class, "hello");
        pointcutMatches(expression, expected[1],Target.class, "hello",String.class);
        pointcutMatches(expression, expected[2],Target.class, "plus",int.class,int.class);
        pointcutMatches(expression, expected[3],Target.class, "minus",int.class,int.class);
        pointcutMatches(expression, expected[4],Target.class, "method");
        pointcutMatches(expression, expected[5],Bean.class, "method");
    }
}
