package com.pht.security.aspect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import com.pht.framework.annotation.Aspect;
import com.pht.framework.annotation.Controller;
import com.pht.framework.proxy.AspectProxy;
import com.pht.security.exception.AuthzException;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import com.pht.security.annotation.Authenticated;
import com.pht.security.annotation.Guest;
import com.pht.security.annotation.HasPermissions;
import com.pht.security.annotation.HasRoles;
import com.pht.security.annotation.User;

/**
 * 授权注解切面
 *
 * @author pht
 * @since 1.0.0
 */
@Aspect(Controller.class)
public class AuthzAnnotationAspect extends AspectProxy {

    /**
     * 定义一个基于授权功能的注解类数组
     */
    private static final Class[] ANNOTATION_CLASS_ARRAY = {
        Authenticated.class, User.class, Guest.class, HasRoles.class, HasPermissions.class
    };

    @Override
    public void before(Class<?> cls, Method method, Object[] params) throws Throwable {
        // 从目标类与目标方法中获取相应的注解
        Annotation annotation = getAnnotation(cls, method);
        if (annotation != null) {
            // 判断授权注解的类型
            Class<?> annotationType = annotation.annotationType();
            if (annotationType.equals(Authenticated.class)) {
                handleAuthenticated();
            } else if (annotationType.equals(User.class)) {
                handleUser();
            } else if (annotationType.equals(Guest.class)) {
                handleGuest();
            } else if (annotationType.equals(HasRoles.class)) {
                handleHasRoles((HasRoles) annotation);
            } else if (annotationType.equals(HasPermissions.class)) {
                handleHasPermissions((HasPermissions) annotation);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private Annotation getAnnotation(Class<?> cls, Method method) {
        // 遍历所有的授权注解
        for (Class<? extends Annotation> annotationClass : ANNOTATION_CLASS_ARRAY) {
            // 首先判断目标方法上是否带有授权注解
            if (method.isAnnotationPresent(annotationClass)) {
                return method.getAnnotation(annotationClass);
            }
            // 然后判断目标类上是否带有授权注解
            if (cls.isAnnotationPresent(annotationClass)) {
                return cls.getAnnotation(annotationClass);
            }
        }
        // 若目标方法与目标类上均未带有授权注解，则返回空对象
        return null;
    }

    private void handleAuthenticated() {
        Subject currentUser = SecurityUtils.getSubject();
        if (!currentUser.isAuthenticated()) {
            throw new AuthzException("当前用户尚未认证");
        }
    }

    private void handleUser() {
        Subject currentUser = SecurityUtils.getSubject();
        PrincipalCollection principals = currentUser.getPrincipals();
        if (principals == null || principals.isEmpty()) {
            throw new AuthzException("当前用户尚未登录");
        }
    }

    private void handleGuest() {
        Subject currentUser = SecurityUtils.getSubject();
        PrincipalCollection principals = currentUser.getPrincipals();
        if (principals != null && !principals.isEmpty()) {
            throw new AuthzException("当前用户不是访客");
        }
    }

    private void handleHasRoles(HasRoles hasRoles) {
        String roleName = hasRoles.value();
        Subject currentUser = SecurityUtils.getSubject();
        if (!currentUser.hasRole(roleName)) {
            throw new AuthzException("当前用户没有指定角色，角色名：" + roleName);
        }
    }

    private void handleHasPermissions(HasPermissions hasPermissions) {
        String permissionName = hasPermissions.value();
        Subject currentUser = SecurityUtils.getSubject();
        if (!currentUser.isPermitted(permissionName)) {
            throw new AuthzException("当前用户没有指定权限，权限名：" + permissionName);
        }
    }
}
