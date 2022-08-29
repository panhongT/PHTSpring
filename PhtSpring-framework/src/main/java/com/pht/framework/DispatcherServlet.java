package com.pht.framework;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Map;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pht.framework.bean.Data;
import com.pht.framework.bean.View;
import com.pht.framework.util.JsonUtil;
import com.pht.framework.util.ReflectionUtil;
import com.pht.framework.util.StringUtil;
import com.pht.framework.bean.Handler;
import com.pht.framework.bean.Param;
import com.pht.framework.helper.BeanHelper;
import com.pht.framework.helper.ConfigHelper;
import com.pht.framework.helper.ControllerHelper;
import com.pht.framework.helper.RequestHelper;
import com.pht.framework.helper.ServletHelper;
import com.pht.framework.helper.UploadHelper;

/**
 * 请求转发器
 *
 * @author pht
 * @since 1.0.0
 */
//@WebServlet 用于将一个类声明为 Servlet，该注解会在部署时被容器处理，容器根据其具体的属性配置将相应的类部署为 Servlet。该注解具有下表给出的一些常用属性。
@WebServlet(urlPatterns = "/*", loadOnStartup = 0)
public class DispatcherServlet extends HttpServlet {

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        //初始化相关Helper类
        HelperLoader.init();
        //获取servletContext对象（用于注册Servlet）
        ServletContext servletContext = servletConfig.getServletContext();

        registerServlet(servletContext);
        //调用文件上传的init方法
        UploadHelper.init(servletContext);
    }

    private void registerServlet(ServletContext servletContext) {
        //注册JSP处理的servlet
        ServletRegistration jspServlet = servletContext.getServletRegistration("jsp");
        jspServlet.addMapping("/index.jsp");
        jspServlet.addMapping(ConfigHelper.getAppJspPath() + "*");
        //注册处理静态资源的默认servlet
        ServletRegistration defaultServlet = servletContext.getServletRegistration("default");
        defaultServlet.addMapping("/favicon.ico");
        defaultServlet.addMapping(ConfigHelper.getAppAssetPath() + "*");
    }

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletHelper.init(request, response);
        try {
            //获取请求方法和请求路径
            String requestMethod = request.getMethod().toLowerCase();
            String requestPath = request.getPathInfo();
            //获取action处理对象（通过参数生成一个request对象，在controller中封装的Action_Map保存了request和handler的关系）
            Handler handler = ControllerHelper.getHandler(requestMethod, requestPath);
            if (handler != null) {
                //获取controller类及其bean实例
                Class<?> controllerClass = handler.getControllerClass();
                Object controllerBean = BeanHelper.getBean(controllerClass);

                //判断是上传类还是请求类参数，并把参数内容封装在param中
                Param param;
                if (UploadHelper.isMultipart(request)) {
                    param = UploadHelper.createParam(request);
                } else {
                    param = RequestHelper.createParam(request);
                }

                Object result;
                Method actionMethod = handler.getActionMethod();
                //根据参数的不同，利用反射调用真正的action方法并获得返回值
                if (param.isEmpty()) {
                    //当注解里面的参数为空的时候可以不传入Action方法中
                    result = ReflectionUtil.invokeMethod(controllerBean, actionMethod);
                } else {
                    result = ReflectionUtil.invokeMethod(controllerBean, actionMethod, param);
                }
                //如果返回结果是属于view类型
                if (result instanceof View) {
                    handleViewResult((View) result, request, response);//跳转到指定JSP页面
                } else if (result instanceof Data) {
                    handleDataResult((Data) result, response);//传Json数据给前端
                }
            }
        } finally {
            ServletHelper.destroy();
        }
    }

    private void handleViewResult(View view, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        //获取转发路径
        String path = view.getPath();
        if (StringUtil.isNotEmpty(path)) {
            if (path.startsWith("/")) {
                response.sendRedirect(request.getContextPath() + path);//重定向
            } else {
                Map<String, Object> model = view.getModel();
                for (Map.Entry<String, Object> entry : model.entrySet()) {
                    request.setAttribute(entry.getKey(), entry.getValue());
                }
                //有数据，只能请求转发
                request.getRequestDispatcher(ConfigHelper.getAppJspPath() + path).forward(request, response);
            }
        }
    }

    /**
     * 此方法有@ResponseBody的效果,即将数据转换为Json，因此使用者不需要加，当然这样可能就有点被局限了，也可以抽取个注解出来
     * @param data
     * @param response
     * @throws IOException
     */
    private void handleDataResult(Data data, HttpServletResponse response) throws IOException {
        Object model = data.getModel();
        if (model != null) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            PrintWriter writer = response.getWriter();
            String json = JsonUtil.toJson(model);
            writer.write(json);
            writer.flush();
            writer.close();
        }
    }
}
