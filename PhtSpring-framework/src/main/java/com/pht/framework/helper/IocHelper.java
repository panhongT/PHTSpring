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
 * @author huangyong
 * @since 1.0.0
 */
public final class IocHelper {

    //这个静态代买块
    static {
        //获取所有的bean类与bean实例之间的映射关系，也就是BEAN MAP
        Map<Class<?>, Object> beanMap = BeanHelper.getBeanMap();
        if (CollectionUtil.isNotEmpty(beanMap)) {
            //遍历 BEAN MAP
            for (Map.Entry<Class<?>, Object> beanEntry : beanMap.entrySet()) {
                //获取BEAN类
                Class<?> beanClass = beanEntry.getKey();
                //获取BEAN实例
                Object beanInstance = beanEntry.getValue();
                //获取BEAN类定义的所有成员变量
                Field[] beanFields = beanClass.getDeclaredFields();
                if (ArrayUtil.isNotEmpty(beanFields)) {//如果成员变量不为空
                    for (Field beanField : beanFields) {
                        //如果是有依赖注入的注解
                        if (beanField.isAnnotationPresent(Inject.class)) {
                            //获取这个需要依赖注入的类名
                            Class<?> beanFieldClass = beanField.getType();
                            //通过类名获得BEAN实例对象并注入给成员变量
                            Object beanFieldInstance = beanMap.get(beanFieldClass);
                            if (beanFieldInstance != null) {
                                //或者就通过反射初始化BEANFILED的值
                                ReflectionUtil.setField(beanInstance, beanField, beanFieldInstance);
                            }
                        }
                    }
                }
            }
        }
    }
}
