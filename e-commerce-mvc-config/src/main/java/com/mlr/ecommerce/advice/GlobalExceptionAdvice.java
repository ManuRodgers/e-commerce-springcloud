package com.mlr.ecommerce.advice;

import com.mlr.ecommerce.vo.CommonResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 *
 *
 * <h2>全局异常捕获处理</h2>
 *
 * @author manurodgers
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionAdvice {

  @ExceptionHandler(value = Exception.class)
  public CommonResponseVo<String> handleCommerceException(
      HttpServletRequest request, Exception exception) {
    CommonResponseVo<String> commonResponseVo =
        CommonResponseVo.<String>builder().code(-1).message("business error again").build();
    commonResponseVo.setData(exception.getMessage());
    log.debug("commerce service has error: [{}],[{}]", exception.getMessage(), exception);
    return commonResponseVo;
  }
}
