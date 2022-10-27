package com.pht.framework.helper;

import java.lang.reflect.Field;
import java.util.Map;

import com.pht.framework.util.CollectionUtil;
import com.pht.framework.annotation.Inject;
import com.pht.framework.util.ArrayUtil;
import com.pht.framework.util.ReflectionUtil;

/**
 * 依赖注入助手类
 *
 * @author pht
 * @since 1.0.0
 */
public final class IocHelper {

    //这个静态代码块会在加载IocHelper这个类的时候加载
    static {
        //获取所有的bean类与bean实例之间的映射关系，也就是BEAN MAP
        Map<Class<?>, Object> beanMap = BeanHelper.getBeanMap();
        if (CollectionUtil.isNotEmpty(beanMap)) {
            //遍历 BEAN MAP
            for (Map.Entry<Class<?>, Object> beanEntry : beanMap.entrySet()) {
                //获取BEAN类
                Class<?> beanClass = beanEntry.getKey();
                //获取BEAN实例，比如为controller类
                Object beanInstance = beanEntry.getValue();
                //获取BEAN类定义的所有成员变量
                Field[] beanFields = beanClass.getDeclaredFields();
                if (ArrayUtil.isNotEmpty(beanFields)) {//如果成员变量不为空
                    for (Field beanField : beanFields) {
                        //如果是有依赖注入的注解
                        if (beanField.isAnnotationPresent(Inject.class)) {
                            //获取这个需要依赖注入的类名
                            Class<?> beanFieldClass = beanField.getType();
                            //通过类名获得BEAN实例对象
                            Object beanFieldInstance = beanMap.get(beanFieldClass);
                            if (beanFieldInstance != null) {
                                //如果该对象不为空证明是在map中，就将获取到的这个实例赋值给此时该类中需要注入的成员变量
                                //比如，这里的beanInstance为controller，beanFieldInstance是service类
                                ReflectionUtil.setField(beanInstance, beanField, beanFieldInstance);
                            }
                        }
                    }
                }
            }
        }
    }
}
