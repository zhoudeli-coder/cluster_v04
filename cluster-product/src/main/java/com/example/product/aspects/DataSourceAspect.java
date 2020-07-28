package com.example.product.aspects;

import com.example.product.annotations.DataSourceAnnotation;
import com.example.product.utils.DynamicDataSourceContextHolder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.mybatis.spring.transaction.SpringManagedTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.jta.JtaTransactionManager;

import java.lang.reflect.Method;

/**
 * 多数据源处理
 *
 * @author DUCHONG
 */
@Aspect
@Order(1)
@Component
public class DataSourceAspect {
    private Logger logger = LoggerFactory.getLogger(DataSourceAspect.class);
    @Autowired
    private ApplicationContext applicationContext;

    @Pointcut("@annotation(com.example.product.annotations.DataSourceAnnotation)")
    public void dsPointCut() {
    }

    @Around("dsPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();

        Method method = signature.getMethod();

        DataSourceAnnotation dataSource = method.getAnnotation(DataSourceAnnotation.class);

        if (null != dataSource) {
            DynamicDataSourceContextHolder.setDataSourceType(dataSource.value().name());
        }

        try {
            return point.proceed();
        } finally {
            DynamicDataSourceContextHolder.clearDataSourceType();
        }
    }
}