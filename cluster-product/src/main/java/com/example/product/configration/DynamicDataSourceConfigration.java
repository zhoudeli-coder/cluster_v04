package com.example.product.configration;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import com.example.common.constant.DataSourceType;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class DynamicDataSourceConfigration {

    @Primary
    @Bean(name = "dynamicDataSource")
    public DynamicDataSourceRouting dynamicDataSource(@Qualifier("masterDataSource") DataSource masterDataSource, @Qualifier("slaveDataSource") DataSource slaveDataSource) {
        Map<Object, Object> targetDataSources = new HashMap<Object, Object>() {{
            put(DataSourceType.MASTER.name(), masterDataSource);
            put(DataSourceType.SLAVE.name(), slaveDataSource);
        }};
        DynamicDataSourceRouting dynamicDataSourceRouting = new DynamicDataSourceRouting();
        dynamicDataSourceRouting.setTargetDataSources(targetDataSources);
        return dynamicDataSourceRouting;
    }

    @Primary
    @Bean(name = "sqlSessionFactoryBean")
    public SqlSessionFactoryBean sqlSessionFactoryBean(@Qualifier("dynamicDataSource") DynamicDataSourceRouting dynamicDataSourceRouting) {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dynamicDataSourceRouting);
        sqlSessionFactoryBean.setConfigLocation(new ClassPathResource("mybatis/mybatis-config.xml"));
        // 此处加入所有Mapper.xml
        List<Resource> mapperLocations = new ArrayList<>();
        mapperLocations.add(new ClassPathResource("mybatis/mapper/CommodityMapper.xml"));
        sqlSessionFactoryBean.setMapperLocations(mapperLocations.toArray(new Resource[mapperLocations.size()]));
        return sqlSessionFactoryBean;
    }

    @Primary
    @Bean(name = "mapperScannerConfigurer")
    public MapperScannerConfigurer mapperScannerConfigurer() {
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        mapperScannerConfigurer.setBasePackage("com.example.product.service.mapper");
        mapperScannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactoryBean");
        return mapperScannerConfigurer;
    }

    @Primary
    @Bean(name = "userTransactionManager", destroyMethod = "close", initMethod = "init")
    public UserTransactionManager userTransactionManager() {
        UserTransactionManager userTransactionManager = new UserTransactionManager();
        userTransactionManager.setForceShutdown(true);
        return userTransactionManager;
    }

    @Primary
    @Bean(name = "userTransactionImp")
    public UserTransactionImp userTransactionImp() {
        return new UserTransactionImp();
    }

    @Primary
    @Bean(name = "JtaTransactionManager")
    public JtaTransactionManager jtaTransactionManager(@Qualifier("userTransactionManager") UserTransactionManager userTransactionManager, @Qualifier("userTransactionImp") UserTransactionImp userTransactionImp) {
        JtaTransactionManager jtaTransactionManager = new JtaTransactionManager();
        jtaTransactionManager.setTransactionManager(userTransactionManager);
        jtaTransactionManager.setUserTransaction(userTransactionImp);
        jtaTransactionManager.setAllowCustomIsolationLevels(true);
        return jtaTransactionManager;
    }
}