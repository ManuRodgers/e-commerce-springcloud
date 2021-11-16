package com.mlr.ecommerce.config;

import com.mlr.ecommerce.filter.LoginUserInfoInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 *
 *
 * <h1>Web Mvc 配置</h1>
 *
 * @author manurodgers
 */
@Configuration
@Slf4j
public class ImoocWebMvcConfig extends WebMvcConfigurationSupport {
  /**
   *
   *
   * <h2>添加拦截器配置</h2>
   */
  @Override
  protected void addInterceptors(InterceptorRegistry registry) {
    // 添加用户身份统一登录拦截的拦截器
    log.info("addInterceptors");
    registry.addInterceptor(new LoginUserInfoInterceptor()).addPathPatterns("/**").order(0);
  }

  /**
   *
   *
   * <h2>让 MVC 加载 Swagger 的静态资源</h2>
   */
  @Override
  protected void addResourceHandlers(ResourceHandlerRegistry registry) {

    registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
    registry
        .addResourceHandler("swagger-ui.html")
        .addResourceLocations("classpath:/META-INF/resources/");
    registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
    registry
        .addResourceHandler("/webjars/**")
        .addResourceLocations("classpath:/META-INF/resources/webjars/");

    super.addResourceHandlers(registry);
  }
}
