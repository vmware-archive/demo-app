package com.wfsample.service;

import com.wfsample.common.dto.PackedShirtsDTO;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * API interface for delivery service.
 *
 * @author Srujan Narkedamalli (snarkedamall@wavefront.com).
 */
@Path("/delivery")
@Produces(MediaType.APPLICATION_JSON)
public interface DeliveryApi {

  @POST
  @Path("{orderNum}")
  @Consumes(MediaType.APPLICATION_JSON)
  Response dispatch(@PathParam("orderNum") String orderNum, PackedShirtsDTO shirts);

  @GET
  @Path("track/{orderNum}")
  Response trackOrder(@PathParam("orderNum") String orderNum);

  @POST
  @Path("cancel/{orderNum}")
  Response cancelOrder(@PathParam("orderNum") String orderNum);
}