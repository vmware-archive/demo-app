package com.wfsample.notification;

import com.wavefront.config.WavefrontReportingConfig;
import com.wavefront.sdk.common.application.ApplicationTags;
import com.wavefront.sdk.jaxrs.server.WavefrontJaxrsDynamicFeature;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import javax.ws.rs.container.DynamicFeature;

import io.opentracing.Tracer;

import java.util.Collections;

import static com.wavefront.config.ReportingUtils.constructApplicationTags;
import static com.wavefront.config.ReportingUtils.constructWavefrontReportingConfig;


/**
 * Main Springboot class for Notification Service.
 *
 * @author Hao Song (songhao@vmware.com).
 */
@SpringBootApplication
public class NotificationApplication {

  public static void main(String[] args) {
    SpringApplication.run(NotificationApplication.class, args);
  }

  @Bean
  public WavefrontJaxrsDynamicFeature wavefrontJaxrsFeatureBean(Environment env) {
    ApplicationTags applicationTags =
        constructApplicationTags(env.getProperty("applicationTagsYamlFile"));
    WavefrontReportingConfig wfReportingConfig =
        constructWavefrontReportingConfig(env.getProperty("wfReportingConfigYamlFile"));
    return new WavefrontJaxrsDynamicFeature(applicationTags, wfReportingConfig,
        Collections.emptySet());
  }

}
