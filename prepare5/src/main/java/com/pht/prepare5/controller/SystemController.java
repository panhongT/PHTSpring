package com.pht.prepare5.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.pht.framework.annotation.Action;
import com.pht.framework.annotation.Controller;
import com.pht.framework.bean.Param;
import com.pht.framework.bean.View;
import com.pht.security.SecurityHelper;
import com.pht.security.exception.AuthcException;

/**
 * 处理系统请求
 *
 */
@Controller
public class SystemController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SystemController.class);

    /**
     * 进入首界面
     */
    @Action("get:/")
    public View index() {
        return new View("index.jsp");
    }

    /**
     * 进入登录界面
     */
    @Action("get:/login")
    public View login() {
        return new View("login.jsp");
    }

    /**
     * 提交登录表单
     */
    @Action("post:/login")
    public View loginSubmit(Param param) {
        String username = param.getString("username");
        String password = param.getString("password");
        try {
            SecurityHelper.login(username, password);
        } catch (AuthcException e) {
            LOGGER.error("login failure", e);
            return new View("/login");
        }
        return new View("/customer");
    }

    /**
     * 提交注销请求
     */
    @Action("get:/logout")
    public View logout() {
        SecurityHelper.logout();
        return new View("/");
    }
}
