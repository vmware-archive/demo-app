package com.wfsample.common.dto;

import java.util.List;

/**
 * DTO for packed beachshirts after the order is completed.
 *
 * @author Srujan Narkedamalli (snarkedamall@wavefront.com).
 */
public class PackedShirtsDTO {
  List<ShirtDTO> shirts;

  public PackedShirtsDTO() {
  }

  public PackedShirtsDTO(List<ShirtDTO> shirts) {
    this.shirts = shirts;
  }

  public List<ShirtDTO> getShirts() {
    return shirts;
  }

  public void setShirts(List<ShirtDTO> shirts) {
    this.shirts = shirts;
  }
}
