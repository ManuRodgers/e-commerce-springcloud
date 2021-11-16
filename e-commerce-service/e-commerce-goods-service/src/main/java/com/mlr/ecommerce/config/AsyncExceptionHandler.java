package com.mlr.ecommerce.config;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

import java.lang.reflect.Method;

/**
 *
 *
 * <h1>异步任务异常捕获处理器</h1>
 */
@SuppressWarnings("all")
@Slf4j
public class AsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

  @Override
  public void handleUncaughtException(Throwable throwable, Method method, Object... objects) {
    throwable.printStackTrace();
    log.error(
        "Async error: {}, Method: {}, Param: {}",
        throwable.getMessage(),
        method.getName(),
        JSON.toJSONString(objects));
    // TODO 发送邮件或者是短信, 做进一步的报警处理
  }
}
