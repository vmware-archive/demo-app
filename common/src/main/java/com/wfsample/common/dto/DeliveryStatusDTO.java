package com.wfsample.common.dto;

/**
 * DTO for delivery status for shirts.
 *
 * @author Srujan Narkedamalli (snarkedamall@wavefront.com).
 */
public class DeliveryStatusDTO {
  String trackingNum;
  String status;

  public DeliveryStatusDTO() {
  }

  public DeliveryStatusDTO(String trackingNum, String status) {
    this.trackingNum = trackingNum;
    this.status = status;
  }

  public String getTrackingNum() {
    return trackingNum;
  }

  public void setTrackingNum(String trackingNum) {
    this.trackingNum = trackingNum;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}
