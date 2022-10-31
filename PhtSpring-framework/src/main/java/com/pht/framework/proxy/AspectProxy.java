package com.pht.framework.proxy;

import java.lang.reflect.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 切面代理
 *
 * @author pht
 * @since 1.0.0
 */
    //用于调用proxyManager
    //提供一个模板方法，并在该抽象类的具体实现中扩展相应的抽象方法
    //实现了Proxy接口
public abstract class AspectProxy implements Proxy {

    private static final Logger logger = LoggerFactory.getLogger(AspectProxy.class);

    @Override
    //从proxyChain参数中获取了目标类、目标方法与方法参数
    public final Object doProxy(ProxyChain proxyChain) throws Throwable {
        Object result = null;

        Class<?> cls = proxyChain.getTargetClass();
        Method method = proxyChain.getTargetMethod();
        Object[] params = proxyChain.getMethodParams();

        begin();
        try {
            //如果代理链不为空
            if (intercept(cls, method, params)) {
                before(cls, method, params);
                result = proxyChain.doProxyChain();
                after(cls, method, params, result);
            } else {
                result = proxyChain.doProxyChain();
            }
        } catch (Exception e) {
            logger.error("proxy failure", e);
            error(cls, method, params, e);
            throw e;
        } finally {
            end();
        }

        return result;
    }

    public void begin() {
    }

    public boolean intercept(Class<?> cls, Method method, Object[] params) throws Throwable {
        return true;
    }

    public void before(Class<?> cls, Method method, Object[] params) throws Throwable {
    }

    public void after(Class<?> cls, Method method, Object[] params, Object result) throws Throwable {
    }

    public void error(Class<?> cls, Method method, Object[] params, Throwable e) {
    }

    public void end() {
    }
}
