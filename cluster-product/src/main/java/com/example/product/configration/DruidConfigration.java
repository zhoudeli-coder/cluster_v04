package com.example.product.configration;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.example.common.constant.DataSourceType;
import com.example.product.properties.StatViewServletProperties;
import com.example.product.properties.WebStatFilterProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.servlet.Filter;
import javax.sql.DataSource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DruidConfigration {

    @Autowired
    private WebStatFilterProperties webStatFilterProperties;

    @Autowired
    private StatViewServletProperties statViewServletProperties;

    @Autowired
    private ApplicationContext applicationContext;

    //绑定数据源配置（主）
    @ConfigurationProperties(prefix = "spring.datasource.druid.master")
    @Bean
    public DataSource masterDataSource() {
        return DataSourceBuilder.create().type(DruidDataSource.class).build();
    }

    //绑定数据源配置（从）
    @ConfigurationProperties(prefix = "spring.datasource.druid.slave")
    @Bean
    @ConditionalOnProperty(prefix = "spring.datasource.druid.slave", name = "enabled", havingValue = "true")
    public DataSource slaveDataSource() {
        return DataSourceBuilder.create().type(DruidDataSource.class).build();
    }


    @Bean(name = "dynamicDataSource")
    @Primary
    public DynamicDataSource dynamicDataSource() {
        Map<Object, Object> targetDataSources = new HashMap<>();
        DataSource masterDataSource = masterDataSource();
        targetDataSources.put(DataSourceType.MASTER.name(), masterDataSource);
        targetDataSources.put(DataSourceType.SLAVE.name(), slaveDataSource());
        return new DynamicDataSource(masterDataSource, targetDataSources);
    }

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
