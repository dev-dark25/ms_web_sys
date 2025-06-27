<template>
  <div class="default_div">
    <h2>防抖节流</h2>
    <h3>防抖：频繁触发，以最后一次或第一次为准；节流：一定时间内，触发多次只执行一次</h3>
    <h3>安装：npm i --save lodash，使用参考Skills.vue</h3>
    <h2>Vue性能优化</h2>
    <h3>异步组件</h3>
    <div>
      常规：import ComA from "../components/ComA.vue"
      Vue2
      components:{
        ComA:() => import("../components/ComA.vue"),
      }
      Vue3：https://developer.aliyun.com/article/1136761
    </div>
    <h3>路由懒加载</h3>
    <h2>引用路径：</h2>
    <div>@/：在vite.config.ts中定义，指向src目录</div>
    <div> ./：表示当前目录下</div>
    <div>../：表示父级目录</div>
    <el-divider />
    <h2>使用Nginx部署vue项目</h2>
    <div>参考nginx.conf文件</div>
    <el-divider />
    <h2>使用Tomcat部署vue项目（不推荐）</h2>
    <div>刷新404问题：在项目根路径下面创建WEB-INF文件夹，并在文件夹里面新建一个web.xml（内容参考Experiences.vue注释代码）</div>
    <!-- <?xml version="1.0" encoding="UTF-8"?>
    <web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
      xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee
http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd" version="3.1" metadata-complete="true">
      <display-name>Router for Tomcat</display-name>
      <error-page>
        <error-code>404</error-code>
        <location>/index.html</location>
      </error-page>
    </web-app> -->
    <el-divider />
    <h2>使用Tomcat部署springboot war包（支持集成vue项目）</h2>
    <h3>JDK8建议使用Tomcat9.0.x版本</h3>
    <h3>Tomcat无法启动、闪退：</h3>
    <div>1，检查端口是否被占用</div>
    <div>2，Tomcat需对应匹配的JDK版本</div>
    <h3>对于免安装版的Tomcat来说，在启动Tomcat时，需要读取环境变量和配置信息，缺少了这些信息，就不能登记环境变量，导致闪退</h3>
    <div>1，在已解压的tomcat的bin文件夹下找到startup.bat，shutdown.bat，右击->编辑。在文件的最上面加入下面两行：</div>
    <div>SET JAVA_HOME=D:\soft\Java\jdk1.8.0_301</div>
    <div>SET TOMCAT_HOME=D:\work\apache-tomcat-10.0.10</div>
    <div>2，或者配置JAVA_HOME及TOMCAT_HOME环境变量</div>
    <h3>Tomcat日志乱码问题：</h3>
    <div>修改conf目录下的文件logging.properties文件：</div>
    <div>默认是：java.util.logging.ConsoleHandler.encoding = UTF-8</div>
    <div>修改为：java.util.logging.ConsoleHandler.encoding = GBK</div>
    <h3>Tomcat日志 -> logs目录</h3>
    <el-divider />
    <h2>直接使用-jar命令部署springboot jar包</h2>
    <el-divider />
    <h2>使用PostgresDB</h2>
    <h3>下载PG库二进制文件：https://www.enterprisedb.com/download-postgresql-binaries</h3>
    <h3>解压</h3>
    <h3>初始化</h3>
    <div>打开cmd，cd至pgsql\bin目录</div>
    <div>initdb -D ../data -E UTF-8 手动初始化数据库，设置编码</div>
    <div>pg_ctl start -l ../logfile -D ../data 启动初始化完成的数据库，把日志记录在logfile中</div>
    <div>createuser -s -r postgres 创建postgres用户</div>
    <div>psql -d postgres 连接数据库</div>
    <h3>其他命令</h3>
    <div>pg_ctl start -l ../logfile -D ../data 启动</div>
    <div>pg_ctl stop -D ../data 停止</div>
    <div>pg_ctl restart -l ../logfile -D ../data 重启</div>
    <h3>使用解压包中自带的pgAdmin4工具连接数据库</h3>
    <el-divider />
    <h2>Logback</h2>
    <div>Logback 是 SpringBoot 的内置日志框架，实际开发中直接引入spring-boot-starter-web依赖即可</div>
    <div>SpringBoot 官方推荐优先使用带有-spring的文件名作为你的日志配置（如使用logback-spring.xml，而不是logback.xml），命名为logback-spring.xml的日志配置文件，
      将 xml放至src/main/resource下面。也可以使用自定义的名称，比如logback-config.xml，只需要在 application.properties
      文件中使用logging.config=classpath:logback-config.xml指定即可。</div>
  </div>
</template>

<script lang='ts' setup>
</script>

<style></style>
