package com.mlr.ecommerce.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 *
 *
 * <h1>通过 nacos 下发动态路由配置, 监听 Nacos 中路由配置变更</h1>
 *
 * @author manurodgers
 */
@Component
@Slf4j
@DependsOn({"gatewayConfig"})
public class DynamicRouteServiceImplByNacos {
  /** Nacos 配置服务 */
  private ConfigService configService;

  @Autowired private DynamicRouteServiceImpl dynamicRouteService;
  /**
   *
   *
   * <h2>Bean 在容器中构造完成之后会执行 init 方法</h2>
   */
  @PostConstruct
  public void init() throws NacosException {
    log.info("gateway route init....");
    configService = initConfigService();
    if (configService == null) {
      log.error("init config service fail");
    }
    final String nacosConfigInfo =
        configService.getConfig(
            GatewayConfig.NACOS_ROUTE_DATA_ID,
            GatewayConfig.NACOS_ROUTE_GROUP,
            GatewayConfig.DEFAULT_TIMEOUT);
    final List<RouteDefinition> routeDefinitionList =
        JSON.parseArray(nacosConfigInfo, RouteDefinition.class);
    if (CollectionUtils.isNotEmpty(routeDefinitionList)) {
      for (RouteDefinition routeDefinition : routeDefinitionList) {
        dynamicRouteService.addRouteDefinition(routeDefinition);
      }
    }

    // 设置监听器
    dynamicRouteByNacosListener(GatewayConfig.NACOS_ROUTE_DATA_ID, GatewayConfig.NACOS_ROUTE_GROUP);
  }
  /**
   *
   *
   * <h2>初始化 Nacos Config</h2>
   */
  private ConfigService initConfigService() throws NacosException {
    Properties properties = new Properties();
    properties.setProperty("serverAddr", GatewayConfig.NACOS_SERVER_ADDR);
    properties.setProperty("namespace", GatewayConfig.NACOS_NAMESPACE);
    return NacosFactory.createConfigService(properties);
  }

  /**
   *
   *
   * <h2>监听 Nacos 下发的动态路由配置</h2>
   */
  private void dynamicRouteByNacosListener(String dataId, String group) {
    try {
      // 给 Nacos Config 客户端增加一个监听器
      configService.addListener(
          dataId,
          group,
          new Listener() {
            /**
             *
             *
             * <h2>自己提供线程池执行操作</h2>
             */
            @Override
            public Executor getExecutor() {
              return null;
            }
            /**
             *
             *
             * <h2>监听器收到配置更新</h2>
             *
             * @param configInfo Nacos中最新的配置定义
             */
            @Override
            public void receiveConfigInfo(String configInfo) {
              log.info("start to update config: [{}]", configInfo);
              final List<RouteDefinition> routeDefinitionList =
                  JSON.parseArray(configInfo, RouteDefinition.class);
              log.info("update route: {}", routeDefinitionList.toString());
              dynamicRouteService.updateList(routeDefinitionList);
            }
          });
    } catch (NacosException nacosException) {
      nacosException.printStackTrace();
    }
  }
}
