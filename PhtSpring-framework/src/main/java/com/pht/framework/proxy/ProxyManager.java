package com.pht.framework.proxy;

import java.lang.reflect.Method;
import java.util.List;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * 代理管理器
 *
 * @author pht
 * @since 1.0.0
 */
public class ProxyManager {

    @SuppressWarnings("unchecked")
    //创建代理对象的方法，输入一个目标类和一组Proxy接口实现，输出一个代理对象。
    public static <T> T createProxy(final Class<?> targetClass, final List<Proxy> proxyList) {
        return (T) Enhancer.create(targetClass, new MethodInterceptor() {
            @Override
            //这个intercept方法就是用来返回一个代理链
            public Object intercept(Object targetObject, Method targetMethod,
                                    Object[] methodParams, MethodProxy methodProxy) throws Throwable {
                return new ProxyChain(targetClass, targetObject, targetMethod,
                        methodProxy, methodParams, proxyList).doProxyChain();
            }
        });
    }
}
