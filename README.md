# 实现了公共模块依赖引用和jpa实现数据库增删改查
- 依赖添加
~~~
    <!--公共的依赖-->
    <dependency>
        <groupId>com.example</groupId>
        <artifactId>cluster-common</artifactId>
        <version>0.0.1-SNAPSHOT</version>
        <scope>compile</scope>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
        <version>2.2.0.RELEASE</version>
    </dependency>
    <!--Mysql 驱动包-->
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <scope>runtime</scope>
    </dependency>
~~~

- 注解支持
~~~
    @EnableJpaRepositories(basePackages = "com.example")
    @EntityScan("com.example.common.entity")
~~~

- 接口实现
~~~
    public interface UserRepository extends JpaRepository<User, Integer> {
    }
~~~

- JpaRepository 方法调用
~~~
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/")
    public String add(User user){
        userRepository.save(user);
        return "添加成功" + user.getId();
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") Integer id) {
        userRepository.deleteById(id);
        return "删除成功";
    }

    @PutMapping("/")
    public String update(User user) {
        userRepository.save(user);
        return "修改成功";
    }

    @GetMapping("/{id}")
    public User get(@PathVariable("id") Integer id) {
        return userRepository.getOne(id);
    }

    @GetMapping("/list")
    public List<User> list(){
        List<User> userList = userRepository.findAll();
        return userList;
    }
~~~

# 实现了RestTemplate 调用cerfification（验证微服务）服务
- 注入RestTemplate Bean
~~~
    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
~~~

- restTemplate 服务调用
~~~
    private static final String REST_URL_PREFIX = "http://localhost:3001/certification/users";
    @Autowired
    private RestTemplate restTemplate;

    @PostMapping("/")
    public String add(User user) {
        try {
            restTemplate.postForObject(REST_URL_PREFIX + "/", user, String.class);
        } catch (Exception e) {
            return String.format("添加失败[%s]", e.getMessage());
        }
        return String.format("添加成功[%s]", user.getId());
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") Integer id) {
        try {
            restTemplate.delete(String.format(REST_URL_PREFIX + "/%s", id), String.class);
        } catch (Exception e) {
            return String.format("删除失败[%s]", e.getMessage());
        }
        return "删除成功";
    }

    @PutMapping("/")
    public String update(User user) {
        try {
            restTemplate.put(REST_URL_PREFIX, user, String.class);
        } catch (Exception e) {
            return String.format("修改失败[%s]", e.getMessage());
        }
        return "修改成功";
    }

    @GetMapping("/{id}")
    public User get(@PathVariable("id") Integer id) {
        try {
            return restTemplate.getForObject(String.format(REST_URL_PREFIX + "/%s", id), User.class);
        } catch (Exception e) {
            return new User(String.format("修改失败[%s]", e.getMessage()), "");
        }
    }
~~~

# cluster-product 集成 mybatis实现增删改查

- 添加依赖
~~~
    <!--导入 mybatis 启动器-->
    <dependency>
        <groupId>org.mybatis.spring.boot</groupId>
        <artifactId>mybatis-spring-boot-starter</artifactId>
        <version>2.1.3</version>
    </dependency>
~~~

- 注解方式实现增删改查
~~~
    @Mapper //或者用@MapperScan(basePackages = "com.example.product.mapper")
    public interface ProviderMapper {
    
        //useGeneratedKeys是否使用自增主键，keyProperty指定实体类中的哪一个属性封装主键值
        @Options(useGeneratedKeys = true, keyProperty = "id")
        @Insert("insert into provider(p_name) values(#{name})")
        int add(Provider provider);
    
        @Delete("delete from provider where p_id=#{id}")
        int delete(Integer id);
    
        @Update("update provider set p_name=#{name} where p_id=#{id}")
        int update(Provider provider);
    
        @Select("select p_id id, p_name name from provider where p_id=#{id}")
        Provider get(Integer id);
    }
~~~

- 配置文件方式实现增删改查

~~~
    // 第一步
    mybatis:
      config-location: classpath:mybatis/mybatis-config.xml
      mapper-locations: classpath:mybatis/mapper/*.xml
~~~

~~~
    // 第二步
    <?xml version="1.0" encoding="UTF-8"?>
    <!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
    <mapper namespace="com.example.product.service.mapper.CommodityMapper" >
        <resultMap id="BaseResultMap" type="com.example.common.entity.Commodity" >
            <id column="c_id" property="id" jdbcType="INTEGER" />
            <result column="c_name" property="name" jdbcType="VARCHAR" />
        </resultMap>
    
        <sql id="BASE_COLUMN_LIST" >
            c_id, c_name
        </sql>
    
        <insert id="insert" useGeneratedKeys="true" keyProperty="id" parameterType="com.example.common.entity.Commodity">
            INSERT INTO commodity(c_name) VALUES (#{name})
        </insert>
    
        <delete id="delete" parameterType="int">
            DELETE FROM commodity WHERE c_id =#{id}
        </delete>
    
        <update id="update" parameterType="com.example.common.entity.Commodity">
            UPDATE commodity SET
            <if test="name != null">
                name = #{name},
            </if>
            WHERE c_id = #{id}
        </update>
    
        <select id="selectOne" parameterType="int" resultMap="BaseResultMap">
            SELECT
            <include refid="BASE_COLUMN_LIST" />
            FROM commodity
            WHERE c_id = #{id}
        </select>
    </mapper>
~~~
~~~
    // 第三步
    @Mapper
    public interface CommodityMapper {
        int insert(Commodity commodity);
    
        int delete(Integer id);
    
        int update(Commodity commodity);
    
        Commodity selectOne(Integer id);
    }
~~~

# Druid 数据源监控 http://localhost:3002/druid/index.html
- 定制Druid 配置
~~~
    druid: # 下面是自定义配置DruidProperties
      driverClassName: com.mysql.cj.jdbc.Driver
      url: jdbc:mysql://127.0.0.1:3306/cluster?serverTimezone=GMT%2B8
      username: root
      password: zxc123456
      #初始化大小
      initialSize: 5
      #最小值
      minIdle: 5
      #最大值
      maxActive: 20
      #最大等待时间，配置获取连接等待超时，时间单位都是毫秒ms
      maxWait: 60000
      #配置间隔多久才进行一次检测，检测需要关闭的空闲连接
      timeBetweenEvictionRunsMillis: 60000
      #配置一个连接在池中最小生存的时间
      minEvictableIdleTimeMillis: 300000
      validationQuery: SELECT 1 FROM DUAL
      testWhileIdle: true
      testOnBorrow: false
      testOnReturn: false
      poolPreparedStatements: true
      # 配置监控统计拦截的filters，去掉后监控界面sql无法统计，
      #'wall'用于防火墙，SpringBoot中没有log4j，我改成了log4j2
      filters: stat,wall,log4j2
      #最大PSCache连接
      maxPoolPreparedStatementPerConnectionSize: 20
      useGlobalDataSourceStat: true
      # 通过connectProperties属性来打开mergeSql功能；慢SQL记录
      connectionProperties: druid.stat.mergeSql=true;druid.stat.slowSqlMillis=500
      # 配置StatFilter
      web-stat-filter:
        #默认为false，设置为true启动
        enabled: true
        urlPattern: "/*"
        exclusions: "*.js,*.gif,*.jpg,*.bmp,*.png,*.css,*.ico,/druid/*"
      #配置StatViewServlet
      stat-view-servlet:
        urlPattern: "/druid/*"
        #允许那些ip
        allow: 127.0.0.1
        loginUsername: admin
        loginPassword: 123456
        #禁止那些ip
        deny: 192.168.1.102
        #是否可以重置
        resetEnable: true
        #启用
        enabled: true
~~~
- 配置类实现 Druid 数据源Bean
~~~
   //绑定数据源配置
    @ConfigurationProperties(prefix = "spring.datasource.druid")
    @Bean
    public DataSource druid() {
        return new DruidDataSource();
    }
~~~
- 配置一个管理后台的Servlet
~~~
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
~~~
- 配置一个监控的filter
~~~
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
~~~

# 切面实现Druid 主从数据源切换
- 配置主从数据库
~~~
 druid: # 下面是自定义配置DruidProperties
      master: # 主数据源
        enabled: true
        name: primary-db
        driverClassName: com.mysql.cj.jdbc.Driver
        url: jdbc:mysql://127.0.0.1:3306/cluster?serverTimezone=GMT%2B8
        username: root
        password: zxc123456
        #初始化大小
        initialSize: 5
        #最小值
        minIdle: 5
        #最大值
        maxActive: 20
        #最大等待时间，配置获取连接等待超时，时间单位都是毫秒ms
        maxWait: 60000
        #配置间隔多久才进行一次检测，检测需要关闭的空闲连接
        timeBetweenEvictionRunsMillis: 60000
        #配置一个连接在池中最小生存的时间
        minEvictableIdleTimeMillis: 300000
        validationQuery: SELECT 1 FROM DUAL
        testWhileIdle: true
        testOnBorrow: false
        testOnReturn: false
        poolPreparedStatements: true
        # 配置监控统计拦截的filters，去掉后监控界面sql无法统计，
        #'wall'用于防火墙，SpringBoot中没有log4j，我改成了log4j2
        filters: stat,wall,log4j2
        #最大PSCache连接
        maxPoolPreparedStatementPerConnectionSize: 20
        useGlobalDataSourceStat: true
        # 通过connectProperties属性来打开mergeSql功能；慢SQL记录
        connectionProperties: druid.stat.mergeSql=true;druid.stat.slowSqlMillis=500
      slave: # 从数据源
        enabled: true
        name: slave-db
        driverClassName: com.mysql.cj.jdbc.Driver
        url: jdbc:mysql://127.0.0.1:3306/cluster_slave?serverTimezone=GMT%2B8
        username: root
        password: zxc123456
        #初始化大小
        initialSize: 5
        #最小值
        minIdle: 5
        #最大值
        maxActive: 20
        #最大等待时间，配置获取连接等待超时，时间单位都是毫秒ms
        maxWait: 60000
        #配置间隔多久才进行一次检测，检测需要关闭的空闲连接
        timeBetweenEvictionRunsMillis: 60000
        #配置一个连接在池中最小生存的时间
        minEvictableIdleTimeMillis: 300000
        validationQuery: SELECT 1 FROM DUAL
        testWhileIdle: true
        testOnBorrow: false
        testOnReturn: false
        poolPreparedStatements: true
        # 配置监控统计拦截的filters，去掉后监控界面sql无法统计，
        #'wall'用于防火墙，SpringBoot中没有log4j，我改成了log4j2
        filters: stat,wall,log4j2
        #最大PSCache连接
        maxPoolPreparedStatementPerConnectionSize: 20
        useGlobalDataSourceStat: true
        # 通过connectProperties属性来打开mergeSql功能；慢SQL记录
        connectionProperties: druid.stat.mergeSql=true;druid.stat.slowSqlMillis=500
~~~
- 定义枚举
~~~
    public enum DataSourceType {
        MASTER, SLAVE
    }
~~~
- 注解类实现
~~~
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface DataSource {
        /**
         * 切换数据源名称
         */
        DataSourceType value() default DataSourceType.MASTER;
    }
~~~
- 切面实现
~~~
    @Aspect
    @Order(1)
    @Component
    public class DataSourceAspect {
        protected Logger logger = LoggerFactory.getLogger(getClass());
    
        @Pointcut("@annotation(com.example.product.annotations.DataSourceAnnotation)")
        public void dsPointCut() {
    
        }
    
        @Around("dsPointCut()")
        public Object around(ProceedingJoinPoint point) throws Throwable {
            MethodSignature signature = (MethodSignature) point.getSignature();
    
            Method method = signature.getMethod();
    
            DataSource dataSource = method.getAnnotation(DataSource.class);
    
            if (null != dataSource) {
                DynamicDataSourceContextHolder.setDataSourceType(dataSource.value().name());
            }
    
            try {
                return point.proceed();
            } finally {
                // 销毁数据源 在执行方法之后
                DynamicDataSourceContextHolder.clearDataSourceType();
            }
        }
    }
~~~
- 注入动态数据源Bean
~~~
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


    @Bean(name = "dynamicDataSourceRouting")
    @Primary
    public DynamicDataSource dynamicDataSourceRouting() {
        Map<Object, Object> targetDataSources = new HashMap<>();
        DataSource masterDataSource = masterDataSource();
        targetDataSources.put(DataSourceType.MASTER.name(), masterDataSource);
        targetDataSources.put(DataSourceType.SLAVE.name(), slaveDataSource());
        return new DynamicDataSource(masterDataSource, targetDataSources);
    }
    注意： 这个地方会出现Bean循环依赖的问题，需要在启动类加上这个注解@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
~~~
- 测试
~~~
    @Mapper
    public interface CommodityMapper {
    
        @DataSource(DataSourceType.MASTER)
        int insert(Commodity commodity);
    
        @DataSource(DataSourceType.MASTER)
        int delete(Integer id);
    
        @DataSource(DataSourceType.MASTER)
        int update(Commodity commodity);
    
        @DataSource(DataSourceType.SLAVE)
        Commodity selectOne(Integer id);
    }
~~~
# 事务管理
- 添加依赖
~~~
        <!--atomikos transaction management-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-jta-atomikos</artifactId>
        </dependency>
~~~
- DataSource, SessionFactory, SessionTemplate Bean注入
~~~
@Configuration
@MapperScan(basePackages = {"com.example.product.service.mapper", "com.example.product.mapper"}, sqlSessionFactoryRef = "masterSessionFactory", sqlSessionTemplateRef = "masterSqlSessionTemplate")
public class MasterDataSourceConfigration {
    @Autowired
    MasterDruidProperties masterDruidProperties;

    //绑定数据源配置（主）
//    @ConfigurationProperties(prefix = "spring.datasource.druid.master")
    @Bean(name = "masterDataSource")
    @Primary
    public DataSource dataSource() throws SQLException {
        // 设置数据库连接
        MysqlXADataSource mysqlXADataSource = new MysqlXADataSource();
        mysqlXADataSource.setUrl(masterDruidProperties.getUrl());
        mysqlXADataSource.setUser(masterDruidProperties.getUsername());
        mysqlXADataSource.setPassword(masterDruidProperties.getPassword());
        mysqlXADataSource.setPinGlobalTxToPhysicalConnection(true);
        // 交给事务管理器进行管理
        AtomikosDataSourceBean atomikosDataSourceBean = new AtomikosDataSourceBean();
        atomikosDataSourceBean.setXaDataSource(mysqlXADataSource);
        atomikosDataSourceBean.setUniqueResourceName("masterDataSource");
        return atomikosDataSourceBean;
    }

    @Primary
    @Bean(name = "masterSessionFactory")
    public SqlSessionFactory masterSessionFactory(@Qualifier("masterDataSource") DataSource dataSource)
            throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mybatis/mapper/*.xml"));
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setMapUnderscoreToCamelCase(true);
        sqlSessionFactoryBean.setConfiguration(configuration);
        return sqlSessionFactoryBean.getObject();
    }

    @Primary
    @Bean(name = "masterSqlSessionTemplate")
    public SqlSessionTemplate masterSqlSessionTemplate(
            @Qualifier("masterSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
~~~
- 注解实现事务管理
~~~
    // 开启事务管理
    启动类 @EnableTransactionManagement 

    // 注解方法实现事务
    @Transactional
    public void transactionManage(Commodity commodity) {
        int insert = commodityMapper.insert(commodity);

        int a = 100/0;
        commodityMapper.delete(3);
//        providerMapper.delete(3);
    }
~~~
