package com.pht.prepare4.aspect;

import java.lang.reflect.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.pht.framework.annotation.Aspect;
import com.pht.framework.annotation.Controller;
import com.pht.framework.proxy.AspectProxy;

/**
 * 拦截 Controller 所有方法
 *
 * @author pht
 * @since 1.0.0
 */
//只需要实现before和after方法就可以在目标方法执行前后添加其他需要执行的代码
@Aspect(Controller.class)
public class ControllerAspect extends AspectProxy {

    private static final Logger LOGGER = LoggerFactory.getLogger(ControllerAspect.class);

    private long begin;

    @Override
    public void before(Class<?> cls, Method method, Object[] params) throws Throwable {
        LOGGER.debug("---------- begin ----------");
        LOGGER.debug(String.format("class: %s", cls.getName()));
        LOGGER.debug(String.format("method: %s", method.getName()));
        begin = System.currentTimeMillis();
    }

    @Override
    public void after(Class<?> cls, Method method, Object[] params, Object result) throws Throwable {
        LOGGER.debug(String.format("time: %dms", System.currentTimeMillis() - begin));
        LOGGER.debug("----------- end -----------");
    }
}
