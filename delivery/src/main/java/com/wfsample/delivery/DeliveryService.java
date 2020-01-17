package com.wfsample.delivery;

import com.wavefront.sdk.jersey.WavefrontJerseyFactory;
import com.wfsample.common.BeachShirtsUtils;
import com.wfsample.service.NotificationApi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

/**
 * Main Springboot class for Delivery Service.
 *
 * @author Hao Song (songhao@vmware.com).
 */
@SpringBootApplication
public class DeliveryService {

  public static void main(String[] args) {
    SpringApplication.run(DeliveryService.class, args);
  }

  @Bean
  public NotificationApi notificationApi(Environment env, WavefrontJerseyFactory factory) {
    String notificationUrl = "http://" + env.getProperty("notificationHost") + ":" +
        env.getProperty("notificationPort");
    return BeachShirtsUtils.createProxyClient(notificationUrl, NotificationApi.class,
        factory.getWavefrontJaxrsClientFilter());
  }

}
