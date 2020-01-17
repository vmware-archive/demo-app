package com.wfsample.service;

import com.wfsample.common.dto.PackedShirtsDTO;
import com.wfsample.common.dto.ShirtStyleDTO;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * API interface for beachshirt styling service.
 *
 * @author Srujan Narkedamalli (snarkedamall@wavefront.com).
 */
@Path("/style")
@Produces(MediaType.APPLICATION_JSON)
public interface StylingApi {

  @GET
  List<ShirtStyleDTO> getAllStyles();

  @GET
  @Path("{id}/make")
  @Consumes(MediaType.APPLICATION_JSON)
  PackedShirtsDTO makeShirts(@PathParam("id") String id, @QueryParam("quantity") int quantity);

  @POST
  @Path("{id}/add")
  Response addStyle(@PathParam("id") String id);

  @POST
  @Path("{id}/update")
  Response restockStyle(@PathParam("id") String id);

  @POST
  @Path("{id}/delete")
  Response deleteStyle(@PathParam("id") String id);
}