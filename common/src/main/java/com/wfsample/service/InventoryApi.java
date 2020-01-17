package com.wfsample.service;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

/**
 * API for Inventory service.
 *
 * @author Srujan Narkedamalli (snarkedamall@wavefront.com).
 */
@Path("/inventory")
public interface InventoryApi {

  @GET
  @Path("available/{itemId}")
  Response available(@PathParam("itemId") String itemId);

  @POST
  @Path("checkout/{cartId}")
  Response checkout(@PathParam("cartId") String cartId);
}
