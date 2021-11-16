package com.mlr.ecommerce.service.async;

import com.mlr.ecommerce.constant.AsyncTaskStatusEnum;
import com.mlr.ecommerce.goods.GoodsVo;
import com.mlr.ecommerce.vo.AsyncTaskVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 *
 *
 * <h1>异步任务执行管理器</h1>
 *
 * 对异步任务进行包装管理, 记录并塞入异步任务执行信息
 *
 * @author manurodgers
 */
@Slf4j
@Component
public class AsyncTaskManager {
  /** 异步任务执行信息容器 */
  private final Map<String, AsyncTaskVo> taskContainer = new HashMap<>(16);

  private final IAsyncService asyncService;

  public AsyncTaskManager(IAsyncService asyncService) {
    this.asyncService = asyncService;
  }

  /**
   *
   *
   * <h2>初始化异步任务</h2>
   */
  public AsyncTaskVo initTask() {
    AsyncTaskVo asyncTaskVo =
        AsyncTaskVo.builder()
            .taskId(UUID.randomUUID().toString())
            .status(AsyncTaskStatusEnum.STARTED)
            .startTime(new Date())
            .build();
    taskContainer.put(asyncTaskVo.getTaskId(), asyncTaskVo);
    return asyncTaskVo;
  }

  /**
   *
   *
   * <h2>提交异步任务</h2>
   */
  public AsyncTaskVo submitTask(List<GoodsVo> goodsVos) {
    AsyncTaskVo asyncTaskVo = initTask();
    asyncService.asyncImportGoods(goodsVos, asyncTaskVo.getTaskId());
    return asyncTaskVo;
  }

  /** 设置异步任务执行状态信息 */
  public void setAsyncTaskVo(AsyncTaskVo asyncTaskVo) {
    taskContainer.put(asyncTaskVo.getTaskId(), asyncTaskVo);
  }

  /** 获取异步任务执行状态信息 */
  public AsyncTaskVo getAsyncTaskVo(String taskId) {
    return taskContainer.get(taskId);
  }
}
