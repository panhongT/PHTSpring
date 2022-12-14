package com.pht.framework.helper;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import com.pht.framework.annotation.Action;
import com.pht.framework.bean.Handler;
import com.pht.framework.bean.Request;
import com.pht.framework.util.ArrayUtil;
import com.pht.framework.util.CollectionUtil;

/**
 * 控制器助手类
 *
 * @author pht
 * @since 1.0.0
 */
public final class ControllerHelper {
    //用于存放请求与处理器的映射关系

    private static final Map<Request, Handler> ACTION_MAP = new HashMap<Request, Handler>();

    static {
        //获得所有被@controller修饰的类
        Set<Class<?>> controllerClassSet = ClassHelper.getControllerClassSet();
        if (CollectionUtil.isNotEmpty(controllerClassSet)) {//如果controller类不为空
            for (Class<?> controllerClass : controllerClassSet) {
                //获得被注解@Controller修饰类中所有的方法
                Method[] methods = controllerClass.getDeclaredMethods();
                if (ArrayUtil.isNotEmpty(methods)) {//如果方法不为空
                    for (Method method : methods) {
                        if (method.isAnnotationPresent(Action.class)) {//如果方法被@action注解修饰
                            //从action注解中获取url映射规则
                            Action action = method.getAnnotation(Action.class);
                            String mapping = action.value();
                            //验证url映射规则
                            //因为value值得组成类似于（"get:/customer_show"），所以通过以下的做法能分离出请求方式和路径
                            if (mapping.matches("\\w+:/\\w*")) {
                                String[] array = mapping.split(":");
                                if (ArrayUtil.isNotEmpty(array) && array.length == 2) {
                                    //获取请求方式
                                    String requestMethod = array[0];
                                    //获取请求路径
                                    String requestPath = array[1];
                                    //request放的是请求方式、请求路径——也就是请求信息
                                    Request request = new Request(requestMethod, requestPath);
                                    //handler放的是controller类的信息、哪个方法——也就是处理对象的信息
                                    Handler handler = new Handler(controllerClass, method);
                                    //初始化ACTION_MAP
                                    ACTION_MAP.put(request, handler);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 获取 Handler
     */
    public static Handler getHandler(String requestMethod, String requestPath) {
        Request request = new Request(requestMethod, requestPath);
        return ACTION_MAP.get(request);
    }
}
