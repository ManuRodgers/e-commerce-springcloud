package com.mlr.ecommerce.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 *
 *
 * <h1>Java8 Predicate 使用方法与思想</h1>
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class PredicateTest {
  public static List<String> MICRO_SERVICE =
      Arrays.asList("nacos", "authority", "gateway", "ribbon", "feign", "hystrix", "e-commerce");

  @Test
  public void testPredicateTest() {
    Predicate<String> letterLengthLimit = s -> s.length() > 5;
    MICRO_SERVICE.stream().filter(letterLengthLimit).forEach(System.out::println);
  }

  @Test
  public void testPredicateAnd() {
    Predicate<String> letterLengthLimit = s -> s.length() > 5;
    Predicate<String> lettersStartWith = s -> s.startsWith("gate");
    MICRO_SERVICE.stream()
        .filter(letterLengthLimit.and(lettersStartWith))
        .forEach(System.out::println);
  }

  /**
   *
   *
   * <h2>or 等同于我们的逻辑或 ||, 多个条件主要一个满足即可</h2>
   */
  @Test
  public void testPredicateOr() {

    Predicate<String> letterLengthLimit = s -> s.length() > 5;
    Predicate<String> letterStartWith = s -> s.startsWith("gate");

    MICRO_SERVICE.stream()
        .filter(letterLengthLimit.or(letterStartWith))
        .forEach(System.out::println);
  }
  /**
   *
   *
   * <h2>negate 等同于我们的逻辑非 !</h2>
   */
  @Test
  public void testPredicateNot() {
    Predicate<String> letterStartWith = s -> s.startsWith("gate");
    MICRO_SERVICE.stream().filter(letterStartWith.negate()).forEach(System.out::println);
  }

  @Test
  public void testPredicateEquals() {
    Predicate<String> equal = s -> Predicate.isEqual("gateway").test(s);
    MICRO_SERVICE.stream().filter(equal).forEach(System.out::println);
  }
}
