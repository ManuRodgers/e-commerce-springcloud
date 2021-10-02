package com.mlr.ecommerce.service.impl;

import com.alibaba.fastjson.JSON;
import com.mlr.ecommerce.constant.AuthorityConstant;
import com.mlr.ecommerce.constant.CommonConstant;
import com.mlr.ecommerce.entity.EcommerceUser;
import com.mlr.ecommerce.repository.EcommerceUserRepository;
import com.mlr.ecommerce.service.IJwtService;
import com.mlr.ecommerce.vo.LoginUserInfo;
import com.mlr.ecommerce.vo.UsernameAndPassword;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.misc.BASE64Decoder;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

/**
 *
 *
 * <h1>JWT 相关服务接口实现</h1>
 *
 * @author manurodgers
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class JwtServiceImpl implements IJwtService {
  @Autowired private EcommerceUserRepository ecommerceUserRepository;
  /**
   *
   *
   * <h2>生成 JWT Token, 使用默认的超时时间</h2>
   *
   * @param username @description
   * @param password @description
   */
  @Override
  public String generateToken(String username, String password) throws Exception {
    return generateToken(username, password, 0);
  }

  /**
   *
   *
   * <h2>生成指定超时时间的 Token, 单位是天</h2>
   *
   * @param username @description
   * @param password @description
   * @param expire @description
   */
  @Override
  public String generateToken(String username, String password, int expire) throws Exception {
    EcommerceUser ecommerceUser =
        ecommerceUserRepository.findByUsernameAndPassword(username, password);
    if (null == ecommerceUser) {
      log.error("can not find user: [{}], [{}]", username, password);
      return null;
    }
    LoginUserInfo loginUserInfo =
        LoginUserInfo.builder()
            .username(ecommerceUser.getUsername())
            .userId(ecommerceUser.getId())
            .build();

    if (expire <= 0) {
      expire = AuthorityConstant.DEFAULT_EXPIRE_DAY;
    }
    ZonedDateTime zonedDateTime =
        LocalDate.now().plus(expire, ChronoUnit.DAYS).atStartOfDay(ZoneId.systemDefault());
    Date expireDate = Date.from(zonedDateTime.toInstant());
    return Jwts.builder()
        .claim(CommonConstant.JWT_USER_INFO_KEY, JSON.toJSONString(loginUserInfo))
        .setId(UUID.randomUUID().toString())
        .setExpiration(expireDate)
        .signWith(generatePrivateKey(), SignatureAlgorithm.RS256)
        .compact();
  }

  /**
   *
   *
   * <h2>注册用户并生成 Token 返回</h2>
   *
   * @param usernameAndPassword @description
   */
  @Override
  public String registerUserAndGenerateToken(UsernameAndPassword usernameAndPassword)
      throws Exception {
    EcommerceUser oldUser =
        ecommerceUserRepository.findByUsername(usernameAndPassword.getUsername());
    if (null != oldUser) {
      log.error("username is registered: [{}]", oldUser.getUsername());
      return null;
    }
    EcommerceUser ecommerceUser =
        EcommerceUser.builder()
            .username(usernameAndPassword.getUsername())
            .password(usernameAndPassword.getPassword())
            .extraInfo("{}")
            .build();
    // 注册一个新用户, 写一条记录到数据表中
    ecommerceUser = ecommerceUserRepository.save(ecommerceUser);
    log.info("ecommerceUser: {}", ecommerceUser);
    log.info(
        "register user success: [{}], [{}]", ecommerceUser.getUsername(), ecommerceUser.getId());
    // 生成 token 并返回
    return generateToken(ecommerceUser.getUsername(), ecommerceUser.getPassword());
  }

  /**
   *
   *
   * <h2>根据本地存储的私钥获取到 PrivateKey 对象</h2>
   */
  private PrivateKey generatePrivateKey() throws Exception {

    PKCS8EncodedKeySpec pkcs8EncodedKeySpec =
        new PKCS8EncodedKeySpec(new BASE64Decoder().decodeBuffer(AuthorityConstant.PRIVATE_KEY));
    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    return keyFactory.generatePrivate(pkcs8EncodedKeySpec);
  }
}
