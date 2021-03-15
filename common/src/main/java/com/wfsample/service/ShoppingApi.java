package com.wfsample.service;

import com.wfsample.common.dto.OrderDTO;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * API interface for Shopping service.
 *
 * @author Han Zhang (zhanghan@vmware.com).
 */
@Path("/shop")
@Produces(APPLICATION_JSON)
public interface ShoppingApi {

  @GET
  @Path("/menu")
  Response getShoppingMenu();

  @POST
  @Path("/order")
  @Consumes(APPLICATION_JSON)
  Response orderShirts(OrderDTO orderDTO);

  @GET
  @Path("/status/{orderNum}")
  Response getOrderStatus();

  @POST
  @Path("/cancel")
  @Consumes(APPLICATION_JSON)
  Response cancelShirtsOrder();

  @POST
  @Path("/inventory/update")
  @Consumes(APPLICATION_JSON)
  Response updateInventory();

  @POST
  @Path("/remove/{id}")
  @Consumes(APPLICATION_JSON)
  Response removeFromMenu(@PathParam("id") String id);
}