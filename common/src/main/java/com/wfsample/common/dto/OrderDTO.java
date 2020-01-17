package com.wfsample.common.dto;

/**
 * DTO for a beachshirts order.
 *
 * @author Srujan Narkedamalli (snarkedamall@wavefront.com).
 */
public class OrderDTO {
  String styleName;
  int quantity;
  PaymentDTO payment;

  public String getStyleName() {
    return styleName;
  }

  public void setStyleName(String styleName) {
    this.styleName = styleName;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

  public PaymentDTO getPayment() {
    return payment;
  }

  public void setPayment(PaymentDTO payment) {
    this.payment = payment;
  }
}
