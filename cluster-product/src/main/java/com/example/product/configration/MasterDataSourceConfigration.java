package com.example.product.configration;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import com.example.product.properties.MasterDruidProperties;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Properties;

@Configuration
//@MapperScan(basePackages = {"com.example.product.service.mapper", "com.example.product.mapper"}, sqlSessionFactoryRef = "masterSessionFactory", sqlSessionTemplateRef = "masterSqlSessionTemplate")
public class MasterDataSourceConfigration {
    @Autowired
    private MasterDruidProperties masterDruidProperties;

    //绑定数据源配置（主）
//    @ConfigurationProperties(prefix = "spring.datasource.druid.master")
    @Bean(name = "masterDataSource")
    @Primary
    public DataSource dataSource() throws SQLException {
//        // 设置数据库连接
//        MysqlXADataSource mysqlXADataSource = new MysqlXADataSource();
//        mysqlXADataSource.setUrl(masterDruidProperties.getUrl());
//        mysqlXADataSource.setUser(masterDruidProperties.getUsername());
//        mysqlXADataSource.setPassword(masterDruidProperties.getPassword());
//        mysqlXADataSource.setPinGlobalTxToPhysicalConnection(true);
//        // 交给事务管理器进行管理
//        Properties properties = new Properties();
//        properties.put("filters", masterDruidProperties.getFilters());
//        properties.put("useGlobalDataSourceStat", masterDruidProperties.getUseGlobalDataSourceStat());
//        AtomikosDataSourceBean atomikosDataSourceBean = new AtomikosDataSourceBean();
//        atomikosDataSourceBean.setXaDataSource(mysqlXADataSource);
//        atomikosDataSourceBean.setXaProperties(properties);
//        atomikosDataSourceBean.setUniqueResourceName("masterDataSource");
//        return atomikosDataSourceBean;
        AtomikosDataSourceBean atomikosDataSourceBean = new AtomikosDataSourceBean();
        Properties buildProperties = buildProperties();
        atomikosDataSourceBean.setXaDataSourceClassName("com.alibaba.druid.pool.xa.DruidXADataSource");
        atomikosDataSourceBean.setUniqueResourceName("masterDataSource");
        atomikosDataSourceBean.setPoolSize(5);
        atomikosDataSourceBean.setXaProperties(buildProperties);
        return atomikosDataSourceBean;
    }

    /**
     * 从配置文件中加载数据源信息
     *
     * @return
     */
    private Properties buildProperties() {
        Properties properties = new Properties();
        properties.put("url", masterDruidProperties.getUrl());
        properties.put("name", masterDruidProperties.getName());
        properties.put("username", masterDruidProperties.getUsername());
        properties.put("password", masterDruidProperties.getPassword());
        properties.put("driverClassName", masterDruidProperties.getDriverClassName());
        properties.put("initialSize", masterDruidProperties.getInitialSize());
        properties.put("maxActive", masterDruidProperties.getMaxActive());
        properties.put("minIdle", masterDruidProperties.getMinIdle());
        properties.put("maxWait", masterDruidProperties.getMaxWait());
        properties.put("poolPreparedStatements", masterDruidProperties.getPoolPreparedStatements());
        properties.put("maxPoolPreparedStatementPerConnectionSize", masterDruidProperties.getMaxPoolPreparedStatementPerConnectionSize());
        properties.put("validationQuery", masterDruidProperties.getValidationQuery());
        properties.put("testOnBorrow", masterDruidProperties.getTestOnBorrow());
        properties.put("testOnReturn", masterDruidProperties.getTestOnReturn());
        properties.put("testWhileIdle", masterDruidProperties.getTestWhileIdle());
        properties.put("timeBetweenEvictionRunsMillis", masterDruidProperties.getTimeBetweenEvictionRunsMillis());
        properties.put("minEvictableIdleTimeMillis", masterDruidProperties.getMinEvictableIdleTimeMillis());
        properties.put("filters", masterDruidProperties.getFilters());
        return properties;
    }

//    @Primary
//    @Bean(name = "masterSessionFactory")
//    public SqlSessionFactory masterSessionFactory(@Qualifier("masterDataSource") DataSource dataSource)
//            throws Exception {
//        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
//        sqlSessionFactoryBean.setDataSource(dataSource);
//        sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mybatis/mapper/*.xml"));
//        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
//        configuration.setMapUnderscoreToCamelCase(true);
//        sqlSessionFactoryBean.setConfiguration(configuration);
//        return sqlSessionFactoryBean.getObject();
//    }
//
//    @Primary
//    @Bean(name = "masterSqlSessionTemplate")
//    public SqlSessionTemplate masterSqlSessionTemplate(
//            @Qualifier("masterSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
//        return new SqlSessionTemplate(sqlSessionFactory);
//    }
}
