package com.wfsample.delivery;

import com.google.common.collect.ImmutableMap;
import com.wfsample.common.BeachShirtsUtils;
import com.wfsample.common.dto.DeliveryStatusDTO;
import com.wfsample.common.dto.PackedShirtsDTO;
import com.wfsample.service.DeliveryApi;
import com.wfsample.service.NotificationApi;

import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.log.Fields;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import javax.ws.rs.core.Response;

/**
 * Controller for delivery service which is responsible for dispatching shirts returning tracking
 * number for a given order.
 *
 * @author Hao Song (songhao@vmware.com).
 */
public class DeliveryController implements DeliveryApi {
	@Autowired
	private NotificationApi notificationApi;
	private final AtomicInteger tracking = new AtomicInteger(0);
	private final AtomicInteger dispatch = new AtomicInteger(0);
	private final AtomicInteger cancel = new AtomicInteger(0);
	private final Tracer tracer;

	private final MemoryLeak memLeak = new MemoryLeak();
	private final int memLeakCycleFrequency = 10;

	public DeliveryController(Tracer tracer) {
		this.tracer = tracer;
	}

	@Override
	public Response dispatch(String orderNum, PackedShirtsDTO packedShirts) {
		int step = dispatch.incrementAndGet() % memLeakCycleFrequency;

		if (step == 0) {
			// clean up memory leak
			memLeak.reset();
			System.gc();
		} else if (step == memLeakCycleFrequency - 4) {
			memLeak.leak(16);
		} else if (step == memLeakCycleFrequency - 3) {
			memLeak.leak(32);
		} else if (step == memLeakCycleFrequency - 2) {
			memLeak.leak(64);
		} else if (step == memLeakCycleFrequency - 1) {
			// leak large amount of memory, request GC, and throw out of memory error
			memLeak.leak(512);
			try {
				for (int i = 0; i < 10; i++) {
					System.gc();
				}
				throw new OutOfMemoryError();
			} catch (Throwable e) {
				Span span = tracer.activeSpan();
				if (span != null) {
					span.log(ImmutableMap.of(
							Fields.EVENT, "error",
							Fields.ERROR_KIND, e.getClass().getName(),
							Fields.STACK, ExceptionUtils.getStackTrace(e)
					));
				}
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
						new DeliveryStatusDTO(null, "delivery dispatch failed")).build();
			}
		}
		BeachShirtsUtils.consumeCpu(70, 0.5);
		String trackingNum = UUID.randomUUID().toString();
		//writeTrackingtoDB()
		System.out.println("Tracking number of Order:" + orderNum + " is " + trackingNum);
		notificationApi.notify(trackingNum);
		return Response.ok(new DeliveryStatusDTO(trackingNum, "shirts delivery dispatched")).build();
	}

	@Override
	public Response trackOrder(String orderNum) {
		if (tracking.incrementAndGet() % 8 == 0) {
			Span span = tracer.activeSpan();
			if (span != null) {
				span.log(ImmutableMap.of(Fields.ERROR_KIND, "order number not found", "orderNum",
						orderNum));
			}
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		BeachShirtsUtils.consumeCpu(30, 0.5);
		return Response.ok().build();
	}

	@Override
	public Response cancelOrder(String orderNum) {
		BeachShirtsUtils.consumeCpu(45, 0.5);
		if (cancel.incrementAndGet() % 7 == 0) {
			Span span = tracer.activeSpan();
			if (span != null) {
				span.log(ImmutableMap.of(Fields.ERROR_KIND, "order has already been cancelled", "orderNum",
						orderNum));
			}
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		return Response.ok().build();
	}

	private static class MemoryLeak {
		private final List<byte[]> bytesLeaked = new ArrayList<>();

		void leak(int megaBytes) {
			bytesLeaked.add(new byte[megaBytes * 1024 * 1024]);
		}

		void reset() {
			bytesLeaked.clear();
		}
	}
}