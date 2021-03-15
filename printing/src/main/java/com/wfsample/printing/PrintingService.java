package com.wfsample.printing;

import com.wavefront.config.ReportingUtils;
import com.wavefront.config.WavefrontReportingConfig;
import com.wavefront.opentracing.WavefrontTracer;
import com.wavefront.opentracing.reporting.WavefrontSpanReporter;
import com.wavefront.sdk.appagent.jvm.reporter.WavefrontJvmReporter;
import com.wavefront.sdk.common.WavefrontSender;
import com.wavefront.sdk.common.application.ApplicationTags;
import com.wavefront.sdk.grpc.WavefrontServerTracerFactory;
import com.wavefront.sdk.grpc.reporter.WavefrontGrpcReporter;
import com.wfsample.beachshirts.AvailableColors;
import com.wfsample.beachshirts.Color;
import com.wfsample.beachshirts.PrintRequest;
import com.wfsample.beachshirts.PrintingGrpc;
import com.wfsample.beachshirts.Shirt;
import com.wfsample.beachshirts.Void;
import com.wfsample.common.BeachShirtsUtils;
import com.wfsample.common.GrpcServiceConfig;

import org.apache.commons.lang3.BooleanUtils;

import java.util.concurrent.atomic.AtomicInteger;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

/**
 * Driver for the printing service which prints a shirt of given style.
 *
 * @author Srujan Narkedamalli (snarkedamall@wavefront.com).
 */
public class PrintingService {

  public PrintingService(GrpcServiceConfig config) throws Exception {
    ApplicationTags applicationTags = ReportingUtils.constructApplicationTags
        (config.getApplicationTagsYamlFile());
    WavefrontReportingConfig wfReportingConfig = ReportingUtils.constructWavefrontReportingConfig(
        config.getWfReportingConfigYamlFile());
    String source = wfReportingConfig.getSource();
    WavefrontSender wavefrontSender = ReportingUtils.constructWavefrontSender(wfReportingConfig);
    WavefrontTracer tracer;
    if (BooleanUtils.isTrue(wfReportingConfig.getReportTraces())) {
      WavefrontSpanReporter wfSpanReporter = new WavefrontSpanReporter.Builder().withSource(source).build(wavefrontSender);
      tracer = (new com.wavefront.opentracing.WavefrontTracer.Builder(wfSpanReporter, applicationTags)).build();
    } else {
      tracer = null;
    }
    WavefrontJvmReporter wfJvmReporter = new WavefrontJvmReporter.Builder(applicationTags).
        withSource(source).build(wavefrontSender);
    wfJvmReporter.start();
    WavefrontGrpcReporter grpcReporter = new WavefrontGrpcReporter.Builder(
        applicationTags).withSource(source).reportingIntervalSeconds(30).build(wavefrontSender);
    grpcReporter.start();
    WavefrontServerTracerFactory tracerFactory =
        new WavefrontServerTracerFactory.Builder(grpcReporter, applicationTags).
            withTracer(tracer).recordStreamingStats().build();
    ServerBuilder builder = ServerBuilder.forPort(config.getGrpcPort()).
        addService(new PrintingImpl(config)).addStreamTracerFactory(tracerFactory);
    Server printingServer = builder.build();
    System.out.println("Starting printing server");
    printingServer.start();
    System.out.println("Started printing server");
    printingServer.awaitTermination();
  }

  public static void main(String[] args) throws Exception {
    GrpcServiceConfig conf = BeachShirtsUtils.scenarioFromFile(args[0]);
    new PrintingService(conf);
  }

  static class PrintingImpl extends PrintingGrpc.PrintingImplBase {
    private final GrpcServiceConfig conf;
    private final AtomicInteger print = new AtomicInteger(0);
    private final AtomicInteger addcolor = new AtomicInteger(0);
    private final AtomicInteger restock = new AtomicInteger(0);
    private final AtomicInteger available = new AtomicInteger(0);

    public PrintingImpl(GrpcServiceConfig grpcServiceConfig) {
      this.conf = grpcServiceConfig;
    }

    @Override
    public void printShirts(PrintRequest request, StreamObserver<Shirt> responseObserver) {
      try {
        Thread.sleep(85);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      if (print.incrementAndGet() % 20 == 0) {
        // not enough ink to print shirts
        responseObserver.onError(Status.RESOURCE_EXHAUSTED.asRuntimeException());
      }
      for (int i = 0; i < request.getQuantity(); i++) {
        responseObserver.onNext(Shirt.newBuilder().setStyle(request.getStyleToPrint()).build());
      }
      responseObserver.onCompleted();
    }

    @Override
    public void addPrintColor(Color request,
                              StreamObserver<com.wfsample.beachshirts.Status> responseObserver) {
      try {
        Thread.sleep(63);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      if (addcolor.incrementAndGet() % 20 == 0) {
        // not enough ink to print shirts
        responseObserver.onError(Status.INTERNAL.asRuntimeException());
      }
      responseObserver.onNext(com.wfsample.beachshirts.Status.newBuilder().setStatus(true).build());
      responseObserver.onCompleted();
    }

    @Override
    public void restockColor(Color request,
                             StreamObserver<com.wfsample.beachshirts.Status> responseObserver) {
      try {
        Thread.sleep(40);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      if (restock.incrementAndGet() % 20 == 0) {
        // not enough ink to print shirts
        responseObserver.onError(Status.UNAVAILABLE.asRuntimeException());
      }
      responseObserver.onNext(com.wfsample.beachshirts.Status.newBuilder().setStatus(true).build());
      responseObserver.onCompleted();
    }

    @Override
    public void getAvailableColors(Void request,
                                   StreamObserver<AvailableColors> responseObserver) {
      try {
        Thread.sleep(20);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      if (available.incrementAndGet() % 40 == 0) {
        // not enough ink to print shirts
        responseObserver.onError(Status.CANCELLED.asRuntimeException());
      }
      responseObserver.onNext(AvailableColors.newBuilder().
          addColors(Color.newBuilder().setColor("rgb").build()).build());
      responseObserver.onCompleted();
    }
  }
}