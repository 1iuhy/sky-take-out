package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 自定义切面类，实现公共字段字段填充处理的逻辑
 */
@Aspect //表示为切面类
@Component  //也是一个bean，交给spring进行管理
@Slf4j  //非必须，记录日志使用
public class AutoFillAspect {
    /**
     * 切入点，通俗说就是对那些类的哪些方法进行拦截
     */
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut() {}
    /*
        execution(
            [修饰符] 返回类型 [类名].方法名(参数类型) [throws 异常]
        )
        [] 表示可选部分，可以省略。
        * 表示通配符，匹配任意内容。
        .. 表示任意多个参数或包路径。

        前面的execution表达式是指com.sky.mapper包下的所有文件的所有返回类型的方法
        &&后面指的是必须含有此注解的值作为切入面
     */

    /**
     * 自定义的前置通知，在通知中进行公共字段的赋值
     */
    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint) {//连接点可以获取到哪个方法被拦截和其参数类型和方法
        log.info("开始进行公共字段的自动填充...");

        //获取当前被拦截的数据库操作的类型
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();//方法签名对象,获得签名信息
        AutoFill autoFill =  signature.getMethod().getAnnotation(AutoFill.class);//通过签名信息获得方法，在获得AutoFill注解
        OperationType operationType = autoFill.value();//获得操作类型
        //获取被拦截方法的参数
        Object[] args = joinPoint.getArgs();//获得连接点方法的参数
        if(args == null||args.length==0){
            return;
        }
        Object entity = args[0];//约定将实体放在参数的第一个位置
        //准备赋值的数据
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();
        //根据操作的类型通过反射进行赋值
        if(operationType==OperationType.INSERT){
            //为四个公共字段进行赋值，使用反射获得set方法
            try {
                //getClass实体的类，通过类获取特定方法，方法中传入方法名称和参数类型的类
                Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalTime.class);
                Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                //反射动态调用
                setCreateTime.invoke(entity, now);
                setCreateUser.invoke(entity, currentId);
                setUpdateTime.invoke(entity, now);
                setUpdateUser.invoke(entity, currentId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if(operationType==OperationType.UPDATE){
            //为两个字段进行赋值，使用反射获得set方法
            try {
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                setUpdateTime.invoke(entity, now);
                setUpdateUser.invoke(entity, currentId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
