package com.wfsample.common;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * gRPC based service configuration.
 *
 * Srujan Narkedamalli (snarkedamall@wavefront.com).
 */
public class GrpcServiceConfig {

  /**
   * Port on which gRPC service will run on.
   */
  @JsonProperty
  private int grpcPort = 0;

  /**
   * Path to the Application tags yaml file.
   */
  @JsonProperty
  private String applicationTagsYamlFile;

  /**
   * Path to the wavefront reporting config yaml file.
   */
  @JsonProperty
  private String wfReportingConfigYamlFile;

  public int getGrpcPort() {
    return grpcPort;
  }

  public String getApplicationTagsYamlFile() {
    return applicationTagsYamlFile;
  }

  public String getWfReportingConfigYamlFile() {
    return wfReportingConfigYamlFile;
  }
}
