package com.mlr.ecommerce.controller;

import com.mlr.ecommerce.goods.GoodsVo;
import com.mlr.ecommerce.service.async.AsyncTaskManager;
import com.mlr.ecommerce.vo.AsyncTaskVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *
 *
 * <h1>异步任务服务对外提供的 API</h1>
 */
@Api(tags = "商品异步入库服务")
@Slf4j
@RestController
@RequestMapping("/async-goods")
public class AsyncGoodsController {
  @Autowired private AsyncTaskManager asyncTaskManager;

  @ApiOperation(
      value = "import goods",
      notes = "import goods into goods table",
      httpMethod = "POST")
  @PostMapping("/import-goods")
  public AsyncTaskVo importGoods(@RequestBody List<GoodsVo> goodsVos) {
    log.info("Asynchronously importing goods");
    return asyncTaskManager.submitTask(goodsVos);
  }

  @ApiOperation(value = "query status", notes = "query status for async task", httpMethod = "GET")
  @GetMapping("/query-task")
  public AsyncTaskVo queryAsyncTaskStatus(@RequestParam("taskId") String taskId) {
    log.info("querying async task info");
    return asyncTaskManager.getAsyncTaskVo(taskId);
  }
}
