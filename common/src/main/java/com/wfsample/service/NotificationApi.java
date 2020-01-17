package com.wfsample.service;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * API interface for notification service.
 *
 * @author Hao Song (songhao@vmware.com).
 */
@Path("/notify")
@Produces(MediaType.APPLICATION_JSON)
public interface NotificationApi {

  @POST
  @Path("/{trackNum}")
  Response notify(@PathParam("trackNum") String trackNum);

}
