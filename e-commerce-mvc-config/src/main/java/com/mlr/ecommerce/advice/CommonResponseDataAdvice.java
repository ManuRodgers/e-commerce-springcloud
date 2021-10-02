package com.mlr.ecommerce.advice;

import com.mlr.ecommerce.annotation.IgnoreResponseAdvice;
import com.mlr.ecommerce.vo.CommonResponseVo;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Objects;

/**
 *
 *
 * <h1>实现统一响应</h1>
 *
 * @author manurodgers
 */
@RestControllerAdvice(value = "com.mlr.ecommerce")
public class CommonResponseDataAdvice implements ResponseBodyAdvice<Object> {
  @Override
  public boolean supports(
      MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
    return !methodParameter.getDeclaringClass().isAnnotationPresent(IgnoreResponseAdvice.class)
        && !Objects.requireNonNull(methodParameter.getMethod())
            .isAnnotationPresent(IgnoreResponseAdvice.class);
  }

  @Override
  @SuppressWarnings("all")
  public Object beforeBodyWrite(
      Object o,
      MethodParameter methodParameter,
      MediaType mediaType,
      Class<? extends HttpMessageConverter<?>> aClass,
      ServerHttpRequest serverHttpRequest,
      ServerHttpResponse serverHttpResponse) {
    CommonResponseVo<Object> commonResponseVo =
        CommonResponseVo.<Object>builder().code(0).message("").build();
    if (o == null) {
      return commonResponseVo;
    } else if (o instanceof CommonResponseVo) {
      commonResponseVo = (CommonResponseVo<Object>) o;
    } else {
      commonResponseVo.setData(o);
    }
    return commonResponseVo;
  }
}
