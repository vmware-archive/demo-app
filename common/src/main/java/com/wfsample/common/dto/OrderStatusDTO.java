package com.wfsample.common.dto;

/**
 * DTO for a beachshirts order status.
 *
 * @author Srujan Narkedamalli (snarkedamall@wavefront.com).
 */
public class OrderStatusDTO {
  String orderId;
  String status;

  public OrderStatusDTO() {
  }

  public OrderStatusDTO(String orderId, String status) {
    this.orderId = orderId;
    this.status = status;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String statusMessage) {
    this.status = statusMessage;
  }

  public String getOrderId() {
    return orderId;
  }

  public void setOrderId(String orderId) {
    this.orderId = orderId;
  }
}
