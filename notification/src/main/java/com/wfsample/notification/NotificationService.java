package com.wfsample.notification;

import com.google.common.collect.ImmutableMap;
import com.wfsample.service.NotificationApi;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.contrib.concurrent.TracedExecutorService;
import io.opentracing.log.Fields;
import io.opentracing.tag.Tags;
import io.opentracing.util.GlobalTracer;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.conn.ConnectTimeoutException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.Response;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Implementation of Notification Service.
 *
 * @author Hao Song (songhao@vmware.com).
 */
@Service
public class NotificationService implements NotificationApi {
  private final Tracer tracer;
  private final ExecutorService notificationExecutor;
  private final AtomicInteger counter = new AtomicInteger(0);

  @Autowired
  public NotificationService() {
    this.tracer = GlobalTracer.get();
    notificationExecutor = new TracedExecutorService(
        Executors.newFixedThreadPool(2 * Runtime.getRuntime().availableProcessors()), tracer);
  }

  public Response notify(String trackNum) {
    Span awsClientSnsSpan = tracer.buildSpan("notifySNS").
            withTag(Tags.SPAN_KIND.getKey(), Tags.SPAN_KIND_CLIENT).withTag(Tags.COMPONENT.getKey(), "java-aws-sdk").
            withTag(Tags.PEER_SERVICE.getKey(), "AmazonSNS").start();
    try (Scope awsClientSnsScope = tracer.activateSpan(awsClientSnsSpan)) {
      try {
        Thread.sleep(50);
        if (counter.incrementAndGet() % 500 == 0) {
          throw new ConnectTimeoutException();
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      } catch (Exception e) {
        Tags.ERROR.set(awsClientSnsSpan, true);
        awsClientSnsSpan.log(ImmutableMap.of(
                Fields.EVENT, "error",
                Fields.ERROR_KIND, e.getClass().getName(),
                Fields.STACK, ExceptionUtils.getStackTrace(e)
        ));
      }
    } finally {
      awsClientSnsSpan.finish();
    }
    notificationExecutor.submit(new InternalNotifyService());
    try {
      Thread.sleep(10);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return Response.accepted().build();
  }

  class InternalNotifyService implements Runnable {
    @Override
    public void run() {
      Span asyncSpan = tracer.buildSpan("asyncNotify").start();
      try (Scope asyncScope = tracer.activateSpan(asyncSpan)) {
        try {
          Thread.sleep(200);
          if (counter.incrementAndGet() % 100 == 0) {
            throw new NullPointerException();
          }
        } catch (InterruptedException e) {
          e.printStackTrace();
        } catch (Exception e) {
          Tags.ERROR.set(asyncSpan, true);
          asyncSpan.log(ImmutableMap.of(
              Fields.EVENT, "error",
              Fields.ERROR_KIND, e.getClass().getName(),
              Fields.STACK, ExceptionUtils.getStackTrace(e)
          ));
        }
      } finally {
        asyncSpan.finish();
      }
    }
  }

}
