package com.pht.framework.proxy;

/**
 * 代理接口
 *
 * @author pht
 * @since 1.0.0
 */
public interface Proxy {

    /**
     * 执行链式代理
     * 传入一个ProxyChain，用于执行链式代理
     * 链式代理：将多个代理通过一条链子串起来，一个个去执行，执行顺序取决于添加顺序
     */
    Object doProxy(ProxyChain proxyChain) throws Throwable;
}
