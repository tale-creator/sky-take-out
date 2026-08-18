package com.sky.aspect;


import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

import static java.time.LocalDateTime.*;

@Component
@Aspect
public class AutoFillAspect {
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut() {
    }

    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint) {
        //获得对象
        Object[] args = joinPoint.getArgs(); // 获取方法参数
        if (args == null || args.length == 0) {
            return; // 如果没有参数，直接返回
        }
        Object entity = args[0]; // 项目约定第一个参数是实体类对象

        //获得方法的注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();  // 获取方法签名
        AutoFill annotation = signature.getMethod().getAnnotation(AutoFill.class);// 获取方法上的注解
        OperationType operationType = annotation.value(); // 获取注解的值（操作类型）

        //提前准备好要赋的值
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();

        //


        // 4.使用反射机制，给实体类对象的公共字段赋值（如创建时间、更新时间、创建人、更新人等）
        if (operationType == OperationType.INSERT) {
            // 插入操作，准备插入时需要填充的字段及其值 (四个字段)
            try {
                // 通过反射获取实体类的公共字段的set方法
                Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                // 调用set方法，给实体类对象的公共字段赋值
                setCreateUser.invoke(entity, currentId);
                setCreateTime.invoke(entity, now);
                setUpdateUser.invoke(entity, currentId);
                setUpdateTime.invoke(entity, now);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        } else if (operationType == OperationType.UPDATE) {
            // 更新操作，准备更新时需要填充的字段及其值 (两个字段)
            try {
                // 通过反射获取实体类的公共字段的set方法
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                // 调用set方法，给实体类对象的公共字段赋值
                setUpdateUser.invoke(entity, currentId);
                setUpdateTime.invoke(entity, now);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }


        }
    }
}
