package com.wfsample.common.dto;

/**
 * DTO for beach shirt.
 *
 * @author Srujan Narkedamalli (snarkedamall@wavefront.com).
 */
public class ShirtDTO {
  ShirtStyleDTO style;

  public ShirtDTO() {
  }

  public ShirtDTO(ShirtStyleDTO style) {
    this.style = style;
  }

  public ShirtStyleDTO getStyle() {
    return style;
  }

  public void setStyle(ShirtStyleDTO style) {
    this.style = style;
  }
}
