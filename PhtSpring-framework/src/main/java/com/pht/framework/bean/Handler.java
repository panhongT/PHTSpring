package com.pht.framework.bean;

import java.lang.reflect.Method;

/**
 * 封装 Action 信息
 *
 * @author pht
 * @since 1.0.0
 */
//封装一个处理对象，获得controller类和被action注解修饰的方法
public class Handler {

    /**
     * Controller 类
     */
    private Class<?> controllerClass;

    /**
     * Action 方法
     */
    private Method actionMethod;

    public Handler(Class<?> controllerClass, Method actionMethod) {
        this.controllerClass = controllerClass;
        this.actionMethod = actionMethod;
    }

    public Class<?> getControllerClass() {
        return controllerClass;
    }

    public Method getActionMethod() {
        return actionMethod;
    }
}
