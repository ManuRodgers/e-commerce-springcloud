package com.mlr.ecommerce.service.async;

import com.mlr.ecommerce.constant.AsyncTaskStatusEnum;
import com.mlr.ecommerce.vo.AsyncTaskVo;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 *
 *
 * <h1>异步任务执行监控切面</h1>
 *
 * @author manurodgers
 */
@Slf4j
@Aspect
@Component
public class AsyncTaskMonitor {
  private final AsyncTaskManager asyncTaskManager;

  public AsyncTaskMonitor(AsyncTaskManager asyncTaskManager) {
    this.asyncTaskManager = asyncTaskManager;
  }

  /**
   *
   *
   * <h2>异步任务执行的环绕切面</h2>
   *
   * 环绕切面让我们可以在方法执行之前和执行之后做一些"额外"的操作
   */
  @Around("execution(* com.mlr.ecommerce.service.async.AsyncServiceImpl.*(..))")
  public Object taskHandle(ProceedingJoinPoint proceedingJoinPoint) {
    //    获取 taskId, 调佣异步任务传入的第二个参数
    final String taskId = proceedingJoinPoint.getArgs()[1].toString();
    AsyncTaskVo asyncTaskVo = asyncTaskManager.getAsyncTaskVo(taskId);
    log.info("AsyncTaskMonitor is monitoring async task: {}", taskId);
    asyncTaskVo.setStatus(AsyncTaskStatusEnum.RUNNING);
    //    设置为运行状态, 并且重新放入容器中
    asyncTaskManager.setAsyncTaskVo(asyncTaskVo);

    AsyncTaskStatusEnum status;
    Object result;
    try {
      //      执行异步任务
      result = proceedingJoinPoint.proceed();
      status = AsyncTaskStatusEnum.SUCCEEDED;

    } catch (Throwable throwable) {
      //      异步任务出现了异常
      result = null;
      status = AsyncTaskStatusEnum.FAILED;
      log.error(
          "AsyncTaskMonitor: async task {} is failed, Error Info: {}",
          taskId,
          throwable.getMessage());
    }
    //    设置异步任务的其他信息， 再次放入容器中
    asyncTaskVo.setEndTime(new Date());
    asyncTaskVo.setStatus(status);
    asyncTaskVo.setTotalTime(
        String.valueOf(asyncTaskVo.getEndTime().getTime() - asyncTaskVo.getStartTime().getTime()));
    asyncTaskManager.setAsyncTaskVo(asyncTaskVo);
    return result;
  }
}
