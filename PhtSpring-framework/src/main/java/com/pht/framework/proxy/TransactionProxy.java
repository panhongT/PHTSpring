package com.pht.framework.proxy;

import java.lang.reflect.Method;

import com.pht.framework.helper.DatabaseHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.pht.framework.annotation.Transaction;

/**
 * 事务代理
 * 编写事务代理切面
 *
 * @author pht
 * @since 1.0.0
 */
public class TransactionProxy implements Proxy {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionProxy.class);
    //这个标志是为了保证同一线程中事务控制相关逻辑只会执行一次。
    private static final ThreadLocal<Boolean> FLAG_HOLDER = new ThreadLocal<Boolean>() {
        @Override
        protected Boolean initialValue() {
            return false;
        }
    };

    @Override
    public Object doProxy(ProxyChain proxyChain) throws Throwable {
        Object result;
        boolean flag = FLAG_HOLDER.get();
        //获取目标方法
        Method method = proxyChain.getTargetMethod();
        //判断是否带有Transaction注解
        if (!flag && method.isAnnotationPresent(Transaction.class)) {
            //先把标志为设为true，保证只执行一次
            FLAG_HOLDER.set(true);
            try {
                //开启事务
                DatabaseHelper.beginTransaction();
                LOGGER.debug("begin transaction");
                //执行目标方法
                result = proxyChain.doProxyChain();
                //提交事务
                DatabaseHelper.commitTransaction();
                LOGGER.debug("commit transaction");
            } catch (Exception e) {
                //若有异常回滚
                DatabaseHelper.rollbackTransaction();
                LOGGER.debug("rollback transaction");
                throw e;
            } finally {
                FLAG_HOLDER.remove();//移除本地线程变量中的标志
            }
        } else {
            result = proxyChain.doProxyChain();
        }
        return result;
    }
}
