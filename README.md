## 多数据源用法
### 下载multidatasources-spring-boot-starter,并安装到本地
### springboot项目中的pom文件加入依赖，地址如下
  ```
  <dependency>
    <groupId>com.yonyou.multidatasources</groupId>
    <artifactId>
      multidatasources-spring-boot-starter
    </artifactId>
    <version>0.9.1-SNAPSHOT</version>
  </dependency>
  ```
### 修改项目的配置文件application.yml，配置自己的数据源，其中datasource下一级约定为default、master、slave,其中default为必配。 当然可以自定义配置，下面的custom支持自定义数据库名称，然后只需要在默认配置的同级配置即可，格式如下。
  ```
  multi: 
    datasource:
      custom: multi1,multi2,multi3.....
      default:
        driverclass: com.mysql.cj.jdbc.Driver
        url: jdbc:mysql://192.168.20.100:3306/community?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf8&useSSL=false&allowPublicKeyRetrieval=true
        username: 
        password: 
      master:
        driverclass: com.mysql.jdbc.Driver
        url: jdbc:mysql://localhost:3306/db_slave1?useSSL=false&useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&allowMultiQueries=true
        username: 
        password: 
      slave:
        driverclass: com.mysql.jdbc.Driver
        url: jdbc:mysql://localhost:3306/db_slave2?useSSL=false&useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&allowMultiQueries=true
        username: 
        password: 
      multi1: 
        driverclass: com.mysql.cj.jdbc.Driver
        url: jdbc:mysql://192.168.20.100:3306/multi?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf8&useSSL=false&allowPublicKeyRetrieval=true
        username: 
        password: 
      multi2: 
        driverclass: com.mysql.cj.jdbc.Driver
        url: jdbc:mysql://192.168.20.100:3306/multi?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf8&useSSL=false&allowPublicKeyRetrieval=true
        username: 
        password: 
  ```
### 在service层的方法上使用注解来切换数据库。
   + 约定配置
      使用枚举中DataSourceKey的DEFAULT, MASTER, SALVE分别对应上面配置数据源default、master、slave.
      示例：
      @TargetDataSource(dataSourceKey = DataSourceKey.MASTER)对应multi.datasource.master配置的数据源
   + 自定义的配置使用
      @TargetDataSource(customDataSourceKey="multi1")对应multi.datasource.multi1配置的自定义数据源
