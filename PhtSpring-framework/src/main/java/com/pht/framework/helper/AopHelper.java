package com.pht.framework.helper;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.pht.framework.annotation.Aspect;
import com.pht.framework.annotation.Service;
import com.pht.framework.proxy.AspectProxy;
import com.pht.framework.proxy.Proxy;
import com.pht.framework.proxy.ProxyManager;
import com.pht.framework.proxy.TransactionProxy;

/**
 * 方法拦截助手类
 *
 * @author huangyong
 * @since 1.0.0
 */
public final class AopHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(AopHelper.class);

    //这个静态块用于初始化AOP 框架
    static {
        try {
            Map<Class<?>, Set<Class<?>>> proxyMap = createProxyMap();
            Map<Class<?>, List<Proxy>> targetMap = createTargetMap(proxyMap);
            for (Map.Entry<Class<?>, List<Proxy>> targetEntry : targetMap.entrySet()) {
                Class<?> targetClass = targetEntry.getKey();
                List<Proxy> proxyList = targetEntry.getValue();
                Object proxy = ProxyManager.createProxy(targetClass, proxyList);//获得代理对象
                BeanHelper.setBean(targetClass, proxy);//把创建出的代理对象放入Bean容器中，类的类型为目标类的类型
            }
        } catch (Exception e) {
            LOGGER.error("aop failure", e);
        }
    }

    //获取代理类和目标类的映射关系
    private static Map<Class<?>, Set<Class<?>>> createProxyMap() throws Exception {
        Map<Class<?>, Set<Class<?>>> proxyMap = new HashMap<Class<?>, Set<Class<?>>>();
        addAspectProxy(proxyMap);//获取所有需要增强的目标类，与代理类的映射关系
        addTransactionProxy(proxyMap);
        return proxyMap;
    }

    private static void addAspectProxy(Map<Class<?>, Set<Class<?>>> proxyMap) throws Exception {
        //获取所有实现了AspectProxy的所有类，也就是代理类
        Set<Class<?>> proxyClassSet = ClassHelper.getClassSetBySuper(AspectProxy.class);
        for (Class<?> proxyClass : proxyClassSet) {
            //是否有Aspect注解，如果有，找出其对应的目标类
            if (proxyClass.isAnnotationPresent(Aspect.class)) {
                Aspect aspect = proxyClass.getAnnotation(Aspect.class);
                Set<Class<?>> targetClassSet = createTargetClassSet(aspect);
                proxyMap.put(proxyClass, targetClassSet);
            }
        }
    }

    private static void addTransactionProxy(Map<Class<?>, Set<Class<?>>> proxyMap) {
        Set<Class<?>> serviceClassSet = ClassHelper.getClassSetByAnnotation(Service.class);
        proxyMap.put(TransactionProxy.class, serviceClassSet);
    }
    //获取所有被注解了的目标类
    private static Set<Class<?>> createTargetClassSet(Aspect aspect) throws Exception {
        Set<Class<?>> targetClassSet = new HashSet<Class<?>>();
        //获取Aspect注解里面的值，也就是根据该注解属性去获取目标类集合
        Class<? extends Annotation> annotation = aspect.value();
        if (annotation != null && !annotation.equals(Aspect.class)) {
            //获取应用包名下带有annotation注解的所有类
            targetClassSet.addAll(ClassHelper.getClassSetByAnnotation(annotation));
        }
        return targetClassSet;
    }

    /**
     * 根据目标类和代理对象的映射关系
     * @param proxyMap
     * @return
     * @throws Exception
     */
    private static Map<Class<?>, List<Proxy>> createTargetMap(Map<Class<?>, Set<Class<?>>> proxyMap) throws Exception {
        //proxyMap里面存放的是代理类（key）和目标类（value）之间的映射关系
        Map<Class<?>, List<Proxy>> targetMap = new HashMap<Class<?>, List<Proxy>>();
        for (Map.Entry<Class<?>, Set<Class<?>>> proxyEntry : proxyMap.entrySet()) {
            Class<?> proxyClass = proxyEntry.getKey();
            Set<Class<?>> targetClassSet = proxyEntry.getValue();
            /*
            从集合中取出每一个需要增强的目标类
             */
            for (Class<?> targetClass : targetClassSet) {
                //为代理类创建实例
                Proxy proxy = (Proxy) proxyClass.newInstance();
                //如果目标类已经在集合中
                if (targetMap.containsKey(targetClass)) {
                    //把代理对象加入目标集合中
                    targetMap.get(targetClass).add(proxy);
                } else {
                    //如果目标类没加入集合
                    //创建一个存放代理对象的链表
                    List<Proxy> proxyList = new ArrayList<Proxy>();
                    proxyList.add(proxy);
                    targetMap.put(targetClass, proxyList);
                }
            }
        }
        return targetMap;
    }
}
