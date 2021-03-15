package com.wfsample.delivery;

import com.google.common.collect.ImmutableMap;
import com.wfsample.common.dto.DeliveryStatusDTO;
import com.wfsample.common.dto.PackedShirtsDTO;
import com.wfsample.common.dto.ShirtDTO;
import com.wfsample.service.DeliveryApi;
import com.wfsample.service.NotificationApi;

import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.log.Fields;
import io.opentracing.rxjava2.TracingObserver;
import io.opentracing.rxjava2.TracingRxJava2Utils;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
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

  private Observer<ShirtDTO> dispatchObserver;
  private Observer<String> asyncCleanUpObserver;

  public DeliveryController(Tracer tracer) {
    this.tracer = tracer;

    TracingRxJava2Utils.enableTracing(tracer);
    this.dispatchObserver = new TracingObserver<>(new Observer<ShirtDTO>() {
      @Override
      public void onSubscribe(Disposable disposable) {
        System.out.println("dispatch started!");
      }

      @Override
      public void onNext(ShirtDTO shirtDTO) {
        try {
          Thread.sleep(5);
          System.out.println(shirtDTO.getStyle().getName() + " processed!");
        } catch (InterruptedException ignored) {
        }
      }

      @Override
      public void onError(Throwable throwable) {
        System.out.println("dispatch error!");
      }

      @Override
      public void onComplete() {
        try {
          Thread.sleep(200);
        } catch (InterruptedException ignored) {
        }
        System.out.println("dispatch completed!");
      }
    }, "dispatchObserver", tracer);

    this.asyncCleanUpObserver = new TracingObserver<>(new Observer<String>() {
      @Override
      public void onSubscribe(Disposable disposable) {
        System.out.println("clean up started!");
      }

      @Override
      public void onNext(String jobName) {
        try {
          Thread.sleep(1000);
          System.out.println("working on " + jobName);
        } catch (InterruptedException ignored) {

        }
      }

      @Override
      public void onError(Throwable throwable) {
        System.out.println("clean up failed!");
      }

      @Override
      public void onComplete() {
        System.out.println("clean up completed!");

      }
    }, "asyncCleanUpObserver", tracer);


  }

  @Override
  public Response dispatch(String orderNum, PackedShirtsDTO packedShirts) {
    int step = dispatch.incrementAndGet() % memLeakCycleFrequency;

    if (step == 0) {
      // clean up memory leak
      memLeak.reset();
      System.gc();
    }
    else if (step == memLeakCycleFrequency - 4) {
      // memLeak.leak(16);
    }
    else if (step == memLeakCycleFrequency - 3) {
      // memLeak.leak(32);
    }
    else if (step == memLeakCycleFrequency - 2) {
      // memLeak.leak(64);
    }
    else if (step == memLeakCycleFrequency - 1) {
      // leak large amount of memory, request GC, and throw out of memory error
      // memLeak.leak(512);
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
    try {
      Thread.sleep(70);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    String trackingNum = UUID.randomUUID().toString();
    System.out.println("Tracking number of Order:" + orderNum + " is " + trackingNum);
    Observable.fromIterable(packedShirts.getShirts()).subscribe(this.dispatchObserver);
    Observable.just("clean up job").subscribeOn(Schedulers.newThread()).subscribe(this.asyncCleanUpObserver);
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
    try {
      Thread.sleep(30);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return Response.ok().build();
  }

  @Override
  public Response cancelOrder(String orderNum) {
    try {
      Thread.sleep(45);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
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