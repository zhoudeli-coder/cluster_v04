package com.example.product.configration;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import com.example.product.properties.SlaveDruidProperties;
import com.mysql.cj.jdbc.MysqlXADataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Properties;

@Configuration
//@MapperScan(basePackages = {"com.example.product.mapper"}, sqlSessionFactoryRef = "slaveSessionFactory", sqlSessionTemplateRef = "slaveSqlSessionTemplate")
public class SlaveDataSourceConfigration {
    @Autowired
    SlaveDruidProperties slaveDruidProperties;

    //绑定数据源配置（从）
//    @ConfigurationProperties(prefix = "spring.datasource.druid.slave")
    @Bean(name = "slaveDataSource")
    @ConditionalOnProperty(prefix = "spring.datasource.druid.slave", name = "enabled", havingValue = "true")
    public DataSource slaveDataSource() throws SQLException {
//        // 设置数据库连接
//        MysqlXADataSource mysqlXADataSource = new MysqlXADataSource();
//        mysqlXADataSource.setUrl(slaveDruidProperties.getUrl());
//        mysqlXADataSource.setUser(slaveDruidProperties.getUsername());
//        mysqlXADataSource.setPassword(slaveDruidProperties.getPassword());
//        mysqlXADataSource.setPinGlobalTxToPhysicalConnection(true);
//        // 交给事务管理器进行管理
//        Properties properties = new Properties();
//        properties.put("filters", slaveDruidProperties.getFilters());
//        properties.put("useGlobalDataSourceStat", slaveDruidProperties.getUseGlobalDataSourceStat());
//        AtomikosDataSourceBean atomikosDataSourceBean = new AtomikosDataSourceBean();
//        atomikosDataSourceBean.setXaDataSource(mysqlXADataSource);
//        atomikosDataSourceBean.setXaProperties(properties);
//        atomikosDataSourceBean.setUniqueResourceName("slaveDataSource");
//        return atomikosDataSourceBean;

        /*上面的方式没办法监控到DataSource， 和 sql查询*/
        AtomikosDataSourceBean atomikosDataSourceBean = new AtomikosDataSourceBean();
        Properties buildProperties = buildProperties();
        atomikosDataSourceBean.setXaDataSourceClassName("com.alibaba.druid.pool.xa.DruidXADataSource");
        atomikosDataSourceBean.setUniqueResourceName("slaveDataSource");
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
        properties.put("url", slaveDruidProperties.getUrl());
        properties.put("name", slaveDruidProperties.getName());
        properties.put("username", slaveDruidProperties.getUsername());
        properties.put("password", slaveDruidProperties.getPassword());
        properties.put("driverClassName", slaveDruidProperties.getDriverClassName());
        properties.put("initialSize", slaveDruidProperties.getInitialSize());
        properties.put("maxActive", slaveDruidProperties.getMaxActive());
        properties.put("minIdle", slaveDruidProperties.getMinIdle());
        properties.put("maxWait", slaveDruidProperties.getMaxWait());
        properties.put("poolPreparedStatements", slaveDruidProperties.getPoolPreparedStatements());
        properties.put("maxPoolPreparedStatementPerConnectionSize", slaveDruidProperties.getMaxPoolPreparedStatementPerConnectionSize());
        properties.put("validationQuery", slaveDruidProperties.getValidationQuery());
        properties.put("testOnBorrow", slaveDruidProperties.getTestOnBorrow());
        properties.put("testOnReturn", slaveDruidProperties.getTestOnReturn());
        properties.put("testWhileIdle", slaveDruidProperties.getTestWhileIdle());
        properties.put("timeBetweenEvictionRunsMillis", slaveDruidProperties.getTimeBetweenEvictionRunsMillis());
        properties.put("minEvictableIdleTimeMillis", slaveDruidProperties.getMinEvictableIdleTimeMillis());
        properties.put("filters", slaveDruidProperties.getFilters());
        return properties;
    }

//    @Bean(name = "slaveSessionFactory")
//    public SqlSessionFactory slaveSessionFactory(@Qualifier("slaveDataSource") DataSource dataSource)
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
//    @Bean(name = "slaveSqlSessionTemplate")
//    public SqlSessionTemplate slaveSqlSessionTemplate(
//            @Qualifier("slaveSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
//        return new SqlSessionTemplate(sqlSessionFactory);
//    }
}
