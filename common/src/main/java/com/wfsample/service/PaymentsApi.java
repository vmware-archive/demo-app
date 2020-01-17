package com.wfsample.service;

import com.wfsample.common.dto.PaymentDTO;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * API for Payments Service.
 *
 * @author Srujan Narkedamalli (snarkedamall@wavefront.com).
 */
public interface PaymentsApi {

    @Path("pay/{orderNum}")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    Response pay(@PathParam("orderNum") String orderNum, PaymentDTO paymentDTO);
}
