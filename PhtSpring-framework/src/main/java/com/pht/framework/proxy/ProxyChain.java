package com.pht.framework.proxy;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import net.sf.cglib.proxy.MethodProxy;

/**
 * 代理链
 *
 * @author pht
 * @since 1.0.0
 */
public class ProxyChain {

    private final Class<?> targetClass;
    private final Object targetObject;
    private final Method targetMethod;
    private final MethodProxy methodProxy;
    private final Object[] methodParams;

    private List<Proxy> proxyList = new ArrayList<Proxy>();
    //充当代理对象的计数器
    //若尚未达到proxyList的上限，则从proxyList中取出相应的Proxy对象，调用其doProxy方法。
    private int proxyIndex = 0;

    public ProxyChain(Class<?> targetClass, Object targetObject, Method targetMethod, MethodProxy methodProxy,
                      Object[] methodParams, List<Proxy> proxyList) {
        this.targetClass = targetClass;
        this.targetObject = targetObject;
        this.targetMethod = targetMethod;
        this.methodProxy = methodProxy;
        this.methodParams = methodParams;
        this.proxyList = proxyList;
    }

    public Object[] getMethodParams() {
        return methodParams;
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }

    public Method getTargetMethod() {
        return targetMethod;
    }

    public Object doProxyChain() throws Throwable {
        Object methodResult;
        if (proxyIndex < proxyList.size()) {
            methodResult = proxyList.get(proxyIndex++).doProxy(this);
        } else {
            methodResult = methodProxy.invokeSuper(targetObject, methodParams);
        }
        return methodResult;
    }
}
