package com.example.product.configration;

import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.example.product.properties.StatViewServletProperties;
import com.example.product.properties.WebStatFilterProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DruidMonitorConfigration {

    @Autowired
    private WebStatFilterProperties webStatFilterProperties;

    @Autowired
    private StatViewServletProperties statViewServletProperties;

    /*** 配置Druid监控 * 1. 配置一个管理后台的Servlet * 2. 配置一个监控的filter */
    @Bean // 1. 配置一个管理后台的Servlet
    public ServletRegistrationBean statViewServlet() {
        //StatViewServlet是 配置管理后台的servlet
        ServletRegistrationBean<StatViewServlet> bean = new ServletRegistrationBean<>(new StatViewServlet(), statViewServletProperties.getUrlPattern());
        //配置初始化参数
        Map<String, String> initParam = new HashMap<>();
        //访问的用户名密码
        initParam.put(StatViewServlet.PARAM_NAME_USERNAME, statViewServletProperties.getLoginUsername());
        initParam.put(StatViewServlet.PARAM_NAME_PASSWORD, statViewServletProperties.getLoginPassword());
        //允许访问的ip，默认所有ip访问
        // initParam.put(StatViewServlet.PARAM_NAME_ALLOW, "");
        //禁止访问的ip
        // initParam.put(StatViewServlet.PARAM_NAME_DENY, "192.168.10.1");
        bean.setInitParameters(initParam);
        return bean;
    }

    @Bean // 配置一个监控的filter
    public FilterRegistrationBean filter() {
        FilterRegistrationBean<Filter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new WebStatFilter());
        //配置初始化参数
        Map<String, String> initParam = new HashMap<>();
        //排除请求
        initParam.put(WebStatFilter.PARAM_NAME_EXCLUSIONS, webStatFilterProperties.getExclusions());
        //拦截所有请求
        bean.setUrlPatterns(Arrays.asList(webStatFilterProperties.getUrlPattern()));
        return bean;
    }
}
