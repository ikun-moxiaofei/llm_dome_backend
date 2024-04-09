package com.mxf.springbootinit.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 全局跨域配置
 *
 */
// 声明该类是一个Spring配置类
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    // 表明下面的方法是从父类或接口中继承或实现的
    @Override
    // 实现WebMvcConfigurer接口中的addCorsMappings方法，该方法用于添加CORS映射，接受一个CorsRegistry对象作为参数
    public void addCorsMappings(CorsRegistry registry) {
        // 向CORS注册表添加一个新的映射，匹配所有URL路径
        registry.addMapping("/**")
                // 允许跨域请求携带凭据（例如cookies）
                .allowCredentials(true)
                // 允许来自任何域的请求，这里的星号表示“所有域”，也可以使用具体的域名
                .allowedOriginPatterns("*")
                // 指定哪些HTTP方法（如GET、POST等）是允许的，这里允许所有列出的方法
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                // 指定哪些HTTP头是允许的，这里的星号表示“所有头”，通常建议只允许必要的头
                .allowedHeaders("*")
                // 指定哪些HTTP头可以被跨域请求访问，这里的星号表示“所有头”，只应该暴露真正需要的头
                .exposedHeaders("*");
    }
}