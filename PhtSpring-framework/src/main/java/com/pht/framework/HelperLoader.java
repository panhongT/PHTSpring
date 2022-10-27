package com.pht.framework;

import com.pht.framework.util.ClassUtil;
import com.pht.framework.helper.AopHelper;
import com.pht.framework.helper.BeanHelper;
import com.pht.framework.helper.ClassHelper;
import com.pht.framework.helper.ControllerHelper;
import com.pht.framework.helper.IocHelper;

/**
 * 加载相应的 Helper 类
 *
 * @author pht
 * @since 1.0.0
 */
//用于加载ClassHelper、BeanHelper、IocHelper、ControllerHelper
public final class HelperLoader {

    public static void init() {
        Class<?>[] classList = {
            ClassHelper.class,
            BeanHelper.class,
            AopHelper.class,
            IocHelper.class,
            ControllerHelper.class
        };
        for (Class<?> cls : classList) {
            ClassUtil.loadClass(cls.getName());
        }
    }
}
