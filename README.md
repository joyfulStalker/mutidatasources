### 多数据源用法(支持约定配置和自定义配置)
#### 下载multidatasources-spring-boot-starter,并安装到本地
#### springboot项目中的pom文件加入依赖，地址如下
  ```
  <dependency>
			<groupId>com.yonyou.cloud.multidatasources</groupId>
			<artifactId>multidatasources-spring-boot-starter</artifactId>
			<version>0.9.1-SNAPSHOT</version>
		</dependency>
  ```
#### 多数据源支持开启和关闭，默认关闭。关闭状态下，使用druid的默认配置。格式如下。
  ```
spring:
  datasource:
    driverClassName: com.mysql.jdbc.Driver
    url: ${db.carowner.community.url:jdbc:mysql://10.180.4.212:3306/carowner_community?useUnicode=true&amp;characterEncoding=utf8mb4}
    username: ${db.carowner.community.username:root}
    password: ${db.carowner.community.password:Pass1234}
    druid:
      ##配置初始化大小、最小、最大 
      initial-size: 1
      min-idle: 1
      max-active: 10
      ##配置获取连接等待超时的时间
      maxWait: 60000
      ##配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
      time-between-eviction-runs-milli: 2000
      ## 配置一个连接在池中最小生存的时间，单位是毫秒
      min-evictable-idle-time-millis: 600000
      max-evictable-idle-time-millis: 900000
      ##其他配置
      validation-query: select 1
      test-while-idle: true
      testOnBorrow: false
      testOnReturn: false
  ```
#### 开启多数据源,需要在配置文件添加配置multi.datasource.enable-dynamic=true。格式如下。
  ```
##多数据源配置
multi: 
  datasource:
    ##是否开启多数据源配置(默认不开启，不配置或配置为false以下配置均不生效)
    enable-dynamic: true 
    ##数据源默认配置
    default-config: 
      ##配置初始化大小、最小、最大 
      initial-size: 1
      min-idle: 1
      max-active: 10
      ##配置获取连接等待超时的时间
      maxWait: 60000
      ##配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
      time-between-eviction-runs-milli: 2000
      ## 配置一个连接在池中最小生存的时间，单位是毫秒
      min-evictable-idle-time-millis: 600000
      max-evictable-idle-time-millis: 900000
      ##其他配置
      validation-query: select 1
      test-while-idle: true
      testOnBorrow: false
      testOnReturn: false
    ##开启多数据源后，这个必须配置，不加注解的走这个
    default:  
      driverclass: com.mysql.jdbc.Driver
      url: ${db.carowner.community.url:jdbc:mysql://10.180.4.212:3306/carowner_community?useUnicode=true&amp;characterEncoding=utf8mb4}
      username: ${db.carowner.community.username: }
      password: ${db.carowner.community.password: }
    ##自定义数据源，多个以分号隔开,可无限扩展
    custom: test1,test2
    ##对应custom中的test1,如有多个按照以下方式依次配置,
    test1: 
      driverclass: com.mysql.jdbc.Driver
      url: jdbc:mysql://localhost:3306/community?useUnicode=true&amp;characterEncoding=utf8mb4
      username: 
      password: 
    test2: 
      driverclass: com.mysql.jdbc.Driver
      url: jdbc:mysql://localhost:3306/community?useUnicode=true&amp;characterEncoding=utf8mb4
      username: 
      password: 
  ```
#### 在service层的方法上使用注解来切换数据库。
   + 约定配置   
     使用枚举中DataSourceKey的DEFAULT, MASTER, SALVE分别对应上面配置数据源default、master、slave.   
     示例：@TargetDataSource(dataSourceKey = DataSourceKey.MASTER)对应multi.datasource.master配置的数据源  
    &emsp;&emsp;&emsp;@TargetDataSource(dataSourceKey = DataSourceKey.DEFAULT)对应multi.datasource.default配置的数据源(使用default的话可以不加注解)
   + 自定义的配置使用   
     示例：@TargetDataSource(customDataSourceKey="test1")对应multi.datasource.test1配置的自定义数据源  
    &emsp;&emsp;&emsp;@TargetDataSource(customDataSourceKey="test2")对应multi.datasource.test2配置的自定义数据源

#### 注1：druid监控配置（多数据源开关与否此配置均有效）
  ```
spring:
  datasource:
    ##下面是监控的配置，需要指定数据源类型  登录地址http://localhost:9990/druid/login.html
    type: com.alibaba.druid.pool.DruidDataSource
    druid:
      WebStatFilter:
        exclusions: '*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*'
      stat-view-servlet:
        ##开启 登录名 登录密码
        enabled: true
        loginUsername: druid
        loginPassword: druid
  ```
#### 注2:使用场景和注意事项（假设web层有webtest方法，service有test1和test2和其他方法）
   + web层方法只调用service层的test1方法或test2方法 数据源以web层调用的这个方法上的注解为准，比如调用test1方法就以test1方法上使用的数据源为准，至于test1调用其他的方法上是否有数据源注解都视为普通方法。
   + web层方法同时调用service层的test1和test2方法，则视为同时操作test1和test2对应的数据库，但是不支持事务回滚。