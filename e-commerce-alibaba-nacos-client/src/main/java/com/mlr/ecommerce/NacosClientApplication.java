package com.mlr.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/** @author manurodgers */
@EnableDiscoveryClient
@SpringBootApplication
public class NacosClientApplication {
  public static void main(String[] args) {
    SpringApplication.run(NacosClientApplication.class, args);
  }
}
