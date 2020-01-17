package com.wfsample.common.dto;

/**
 * DTO for a beach shirt style.
 *
 * @author Srujan Narkedamalli (snarkedamall@wavefront.com).
 */
public class ShirtStyleDTO {
  String name;
  String imageUrl;

  public ShirtStyleDTO() {
  }

  public ShirtStyleDTO(String name, String imageUrl) {
    this.name = name;
    this.imageUrl = imageUrl;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }
}
