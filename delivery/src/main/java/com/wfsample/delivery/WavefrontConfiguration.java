package com.wfsample.delivery;

import com.wavefront.sdk.jersey.WavefrontJerseyFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * Configuration for wavefront sdks.
 *
 * @author Srujan Narkedamalli (snarkedamall@wavefront.com).
 */
@Configuration
public class WavefrontConfiguration {

  @Bean
  public WavefrontJerseyFactory wavefrontJerseyFactory(Environment env){
    return new WavefrontJerseyFactory(env.getProperty("applicationTagsYamlFile"),
        env.getProperty("wfReportingConfigYamlFile"));
  }
}
