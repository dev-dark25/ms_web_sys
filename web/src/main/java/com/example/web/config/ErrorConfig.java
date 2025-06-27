package com.example.web.config;

import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.boot.web.server.ErrorPageRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class ErrorConfig implements ErrorPageRegistrar {

    /**
     * 设置出现错误界面时重定向到static/index.html
     * Vue3 route使用history模式时，必须配置；hash模式则不需要
     */
    public void registerErrorPages(ErrorPageRegistry registry) {
        ErrorPage notFoundPage = new ErrorPage(HttpStatus.NOT_FOUND, "/index.html");    // 定义404跳转错误页面
        registry.addErrorPages(notFoundPage);   // 注册添加notFoundPage
        // 添加其他页面..
    }
}
