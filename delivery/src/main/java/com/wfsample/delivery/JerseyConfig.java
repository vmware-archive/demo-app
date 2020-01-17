package com.wfsample.delivery;

import com.wavefront.sdk.jersey.WavefrontJerseyFactory;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.ApplicationPath;

/**
 * Jersey Configuration class for Delivery Service.
 *
 * @author Hao Song (songhao@vmware.com).
 */
@Component
@ApplicationPath("/")
public class JerseyConfig extends ResourceConfig {

  @Inject
  public JerseyConfig(WavefrontJerseyFactory wavefrontJerseyFactory) {
    register(wavefrontJerseyFactory.getWavefrontJerseyFilter());
    register(new DeliveryController(wavefrontJerseyFactory.getTracer()));
  }

}