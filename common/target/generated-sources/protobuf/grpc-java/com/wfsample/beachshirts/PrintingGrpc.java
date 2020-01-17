package com.wfsample.beachshirts;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.12.0)",
    comments = "Source: beachshirts.proto")
public final class PrintingGrpc {

  private PrintingGrpc() {}

  public static final String SERVICE_NAME = "Printing";

  // Static method descriptors that strictly reflect the proto.
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getPrintShirtsMethod()} instead. 
  public static final io.grpc.MethodDescriptor<com.wfsample.beachshirts.PrintRequest,
      com.wfsample.beachshirts.Shirt> METHOD_PRINT_SHIRTS = getPrintShirtsMethodHelper();

  private static volatile io.grpc.MethodDescriptor<com.wfsample.beachshirts.PrintRequest,
      com.wfsample.beachshirts.Shirt> getPrintShirtsMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<com.wfsample.beachshirts.PrintRequest,
      com.wfsample.beachshirts.Shirt> getPrintShirtsMethod() {
    return getPrintShirtsMethodHelper();
  }

  private static io.grpc.MethodDescriptor<com.wfsample.beachshirts.PrintRequest,
      com.wfsample.beachshirts.Shirt> getPrintShirtsMethodHelper() {
    io.grpc.MethodDescriptor<com.wfsample.beachshirts.PrintRequest, com.wfsample.beachshirts.Shirt> getPrintShirtsMethod;
    if ((getPrintShirtsMethod = PrintingGrpc.getPrintShirtsMethod) == null) {
      synchronized (PrintingGrpc.class) {
        if ((getPrintShirtsMethod = PrintingGrpc.getPrintShirtsMethod) == null) {
          PrintingGrpc.getPrintShirtsMethod = getPrintShirtsMethod = 
              io.grpc.MethodDescriptor.<com.wfsample.beachshirts.PrintRequest, com.wfsample.beachshirts.Shirt>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(
                  "Printing", "printShirts"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.wfsample.beachshirts.PrintRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.wfsample.beachshirts.Shirt.getDefaultInstance()))
                  .setSchemaDescriptor(new PrintingMethodDescriptorSupplier("printShirts"))
                  .build();
          }
        }
     }
     return getPrintShirtsMethod;
  }
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getAddPrintColorMethod()} instead. 
  public static final io.grpc.MethodDescriptor<com.wfsample.beachshirts.Color,
      com.wfsample.beachshirts.Status> METHOD_ADD_PRINT_COLOR = getAddPrintColorMethodHelper();

  private static volatile io.grpc.MethodDescriptor<com.wfsample.beachshirts.Color,
      com.wfsample.beachshirts.Status> getAddPrintColorMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<com.wfsample.beachshirts.Color,
      com.wfsample.beachshirts.Status> getAddPrintColorMethod() {
    return getAddPrintColorMethodHelper();
  }

  private static io.grpc.MethodDescriptor<com.wfsample.beachshirts.Color,
      com.wfsample.beachshirts.Status> getAddPrintColorMethodHelper() {
    io.grpc.MethodDescriptor<com.wfsample.beachshirts.Color, com.wfsample.beachshirts.Status> getAddPrintColorMethod;
    if ((getAddPrintColorMethod = PrintingGrpc.getAddPrintColorMethod) == null) {
      synchronized (PrintingGrpc.class) {
        if ((getAddPrintColorMethod = PrintingGrpc.getAddPrintColorMethod) == null) {
          PrintingGrpc.getAddPrintColorMethod = getAddPrintColorMethod = 
              io.grpc.MethodDescriptor.<com.wfsample.beachshirts.Color, com.wfsample.beachshirts.Status>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "Printing", "addPrintColor"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.wfsample.beachshirts.Color.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.wfsample.beachshirts.Status.getDefaultInstance()))
                  .setSchemaDescriptor(new PrintingMethodDescriptorSupplier("addPrintColor"))
                  .build();
          }
        }
     }
     return getAddPrintColorMethod;
  }
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getRestockColorMethod()} instead. 
  public static final io.grpc.MethodDescriptor<com.wfsample.beachshirts.Color,
      com.wfsample.beachshirts.Status> METHOD_RESTOCK_COLOR = getRestockColorMethodHelper();

  private static volatile io.grpc.MethodDescriptor<com.wfsample.beachshirts.Color,
      com.wfsample.beachshirts.Status> getRestockColorMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<com.wfsample.beachshirts.Color,
      com.wfsample.beachshirts.Status> getRestockColorMethod() {
    return getRestockColorMethodHelper();
  }

  private static io.grpc.MethodDescriptor<com.wfsample.beachshirts.Color,
      com.wfsample.beachshirts.Status> getRestockColorMethodHelper() {
    io.grpc.MethodDescriptor<com.wfsample.beachshirts.Color, com.wfsample.beachshirts.Status> getRestockColorMethod;
    if ((getRestockColorMethod = PrintingGrpc.getRestockColorMethod) == null) {
      synchronized (PrintingGrpc.class) {
        if ((getRestockColorMethod = PrintingGrpc.getRestockColorMethod) == null) {
          PrintingGrpc.getRestockColorMethod = getRestockColorMethod = 
              io.grpc.MethodDescriptor.<com.wfsample.beachshirts.Color, com.wfsample.beachshirts.Status>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "Printing", "restockColor"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.wfsample.beachshirts.Color.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.wfsample.beachshirts.Status.getDefaultInstance()))
                  .setSchemaDescriptor(new PrintingMethodDescriptorSupplier("restockColor"))
                  .build();
          }
        }
     }
     return getRestockColorMethod;
  }
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  @java.lang.Deprecated // Use {@link #getGetAvailableColorsMethod()} instead. 
  public static final io.grpc.MethodDescriptor<com.wfsample.beachshirts.Void,
      com.wfsample.beachshirts.AvailableColors> METHOD_GET_AVAILABLE_COLORS = getGetAvailableColorsMethodHelper();

  private static volatile io.grpc.MethodDescriptor<com.wfsample.beachshirts.Void,
      com.wfsample.beachshirts.AvailableColors> getGetAvailableColorsMethod;

  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static io.grpc.MethodDescriptor<com.wfsample.beachshirts.Void,
      com.wfsample.beachshirts.AvailableColors> getGetAvailableColorsMethod() {
    return getGetAvailableColorsMethodHelper();
  }

  private static io.grpc.MethodDescriptor<com.wfsample.beachshirts.Void,
      com.wfsample.beachshirts.AvailableColors> getGetAvailableColorsMethodHelper() {
    io.grpc.MethodDescriptor<com.wfsample.beachshirts.Void, com.wfsample.beachshirts.AvailableColors> getGetAvailableColorsMethod;
    if ((getGetAvailableColorsMethod = PrintingGrpc.getGetAvailableColorsMethod) == null) {
      synchronized (PrintingGrpc.class) {
        if ((getGetAvailableColorsMethod = PrintingGrpc.getGetAvailableColorsMethod) == null) {
          PrintingGrpc.getGetAvailableColorsMethod = getGetAvailableColorsMethod = 
              io.grpc.MethodDescriptor.<com.wfsample.beachshirts.Void, com.wfsample.beachshirts.AvailableColors>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "Printing", "getAvailableColors"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.wfsample.beachshirts.Void.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.wfsample.beachshirts.AvailableColors.getDefaultInstance()))
                  .setSchemaDescriptor(new PrintingMethodDescriptorSupplier("getAvailableColors"))
                  .build();
          }
        }
     }
     return getGetAvailableColorsMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static PrintingStub newStub(io.grpc.Channel channel) {
    return new PrintingStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static PrintingBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new PrintingBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static PrintingFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new PrintingFutureStub(channel);
  }

  /**
   */
  public static abstract class PrintingImplBase implements io.grpc.BindableService {

    /**
     */
    public void printShirts(com.wfsample.beachshirts.PrintRequest request,
        io.grpc.stub.StreamObserver<com.wfsample.beachshirts.Shirt> responseObserver) {
      asyncUnimplementedUnaryCall(getPrintShirtsMethodHelper(), responseObserver);
    }

    /**
     */
    public void addPrintColor(com.wfsample.beachshirts.Color request,
        io.grpc.stub.StreamObserver<com.wfsample.beachshirts.Status> responseObserver) {
      asyncUnimplementedUnaryCall(getAddPrintColorMethodHelper(), responseObserver);
    }

    /**
     */
    public void restockColor(com.wfsample.beachshirts.Color request,
        io.grpc.stub.StreamObserver<com.wfsample.beachshirts.Status> responseObserver) {
      asyncUnimplementedUnaryCall(getRestockColorMethodHelper(), responseObserver);
    }

    /**
     */
    public void getAvailableColors(com.wfsample.beachshirts.Void request,
        io.grpc.stub.StreamObserver<com.wfsample.beachshirts.AvailableColors> responseObserver) {
      asyncUnimplementedUnaryCall(getGetAvailableColorsMethodHelper(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getPrintShirtsMethodHelper(),
            asyncServerStreamingCall(
              new MethodHandlers<
                com.wfsample.beachshirts.PrintRequest,
                com.wfsample.beachshirts.Shirt>(
                  this, METHODID_PRINT_SHIRTS)))
          .addMethod(
            getAddPrintColorMethodHelper(),
            asyncUnaryCall(
              new MethodHandlers<
                com.wfsample.beachshirts.Color,
                com.wfsample.beachshirts.Status>(
                  this, METHODID_ADD_PRINT_COLOR)))
          .addMethod(
            getRestockColorMethodHelper(),
            asyncUnaryCall(
              new MethodHandlers<
                com.wfsample.beachshirts.Color,
                com.wfsample.beachshirts.Status>(
                  this, METHODID_RESTOCK_COLOR)))
          .addMethod(
            getGetAvailableColorsMethodHelper(),
            asyncUnaryCall(
              new MethodHandlers<
                com.wfsample.beachshirts.Void,
                com.wfsample.beachshirts.AvailableColors>(
                  this, METHODID_GET_AVAILABLE_COLORS)))
          .build();
    }
  }

  /**
   */
  public static final class PrintingStub extends io.grpc.stub.AbstractStub<PrintingStub> {
    private PrintingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private PrintingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected PrintingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new PrintingStub(channel, callOptions);
    }

    /**
     */
    public void printShirts(com.wfsample.beachshirts.PrintRequest request,
        io.grpc.stub.StreamObserver<com.wfsample.beachshirts.Shirt> responseObserver) {
      asyncServerStreamingCall(
          getChannel().newCall(getPrintShirtsMethodHelper(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void addPrintColor(com.wfsample.beachshirts.Color request,
        io.grpc.stub.StreamObserver<com.wfsample.beachshirts.Status> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getAddPrintColorMethodHelper(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void restockColor(com.wfsample.beachshirts.Color request,
        io.grpc.stub.StreamObserver<com.wfsample.beachshirts.Status> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getRestockColorMethodHelper(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getAvailableColors(com.wfsample.beachshirts.Void request,
        io.grpc.stub.StreamObserver<com.wfsample.beachshirts.AvailableColors> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGetAvailableColorsMethodHelper(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class PrintingBlockingStub extends io.grpc.stub.AbstractStub<PrintingBlockingStub> {
    private PrintingBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private PrintingBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected PrintingBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new PrintingBlockingStub(channel, callOptions);
    }

    /**
     */
    public java.util.Iterator<com.wfsample.beachshirts.Shirt> printShirts(
        com.wfsample.beachshirts.PrintRequest request) {
      return blockingServerStreamingCall(
          getChannel(), getPrintShirtsMethodHelper(), getCallOptions(), request);
    }

    /**
     */
    public com.wfsample.beachshirts.Status addPrintColor(com.wfsample.beachshirts.Color request) {
      return blockingUnaryCall(
          getChannel(), getAddPrintColorMethodHelper(), getCallOptions(), request);
    }

    /**
     */
    public com.wfsample.beachshirts.Status restockColor(com.wfsample.beachshirts.Color request) {
      return blockingUnaryCall(
          getChannel(), getRestockColorMethodHelper(), getCallOptions(), request);
    }

    /**
     */
    public com.wfsample.beachshirts.AvailableColors getAvailableColors(com.wfsample.beachshirts.Void request) {
      return blockingUnaryCall(
          getChannel(), getGetAvailableColorsMethodHelper(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class PrintingFutureStub extends io.grpc.stub.AbstractStub<PrintingFutureStub> {
    private PrintingFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private PrintingFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected PrintingFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new PrintingFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.wfsample.beachshirts.Status> addPrintColor(
        com.wfsample.beachshirts.Color request) {
      return futureUnaryCall(
          getChannel().newCall(getAddPrintColorMethodHelper(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.wfsample.beachshirts.Status> restockColor(
        com.wfsample.beachshirts.Color request) {
      return futureUnaryCall(
          getChannel().newCall(getRestockColorMethodHelper(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.wfsample.beachshirts.AvailableColors> getAvailableColors(
        com.wfsample.beachshirts.Void request) {
      return futureUnaryCall(
          getChannel().newCall(getGetAvailableColorsMethodHelper(), getCallOptions()), request);
    }
  }

  private static final int METHODID_PRINT_SHIRTS = 0;
  private static final int METHODID_ADD_PRINT_COLOR = 1;
  private static final int METHODID_RESTOCK_COLOR = 2;
  private static final int METHODID_GET_AVAILABLE_COLORS = 3;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final PrintingImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(PrintingImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_PRINT_SHIRTS:
          serviceImpl.printShirts((com.wfsample.beachshirts.PrintRequest) request,
              (io.grpc.stub.StreamObserver<com.wfsample.beachshirts.Shirt>) responseObserver);
          break;
        case METHODID_ADD_PRINT_COLOR:
          serviceImpl.addPrintColor((com.wfsample.beachshirts.Color) request,
              (io.grpc.stub.StreamObserver<com.wfsample.beachshirts.Status>) responseObserver);
          break;
        case METHODID_RESTOCK_COLOR:
          serviceImpl.restockColor((com.wfsample.beachshirts.Color) request,
              (io.grpc.stub.StreamObserver<com.wfsample.beachshirts.Status>) responseObserver);
          break;
        case METHODID_GET_AVAILABLE_COLORS:
          serviceImpl.getAvailableColors((com.wfsample.beachshirts.Void) request,
              (io.grpc.stub.StreamObserver<com.wfsample.beachshirts.AvailableColors>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class PrintingBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    PrintingBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.wfsample.beachshirts.BeachShirts.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("Printing");
    }
  }

  private static final class PrintingFileDescriptorSupplier
      extends PrintingBaseDescriptorSupplier {
    PrintingFileDescriptorSupplier() {}
  }

  private static final class PrintingMethodDescriptorSupplier
      extends PrintingBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    PrintingMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (PrintingGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new PrintingFileDescriptorSupplier())
              .addMethod(getPrintShirtsMethodHelper())
              .addMethod(getAddPrintColorMethodHelper())
              .addMethod(getRestockColorMethodHelper())
              .addMethod(getGetAvailableColorsMethodHelper())
              .build();
        }
      }
    }
    return result;
  }
}
