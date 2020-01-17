package com.wfsample.packaging;

import com.google.protobuf.ByteString;
import com.wavefront.config.ReportingUtils;
import com.wavefront.config.WavefrontReportingConfig;
import com.wavefront.opentracing.WavefrontTracer;
import com.wavefront.opentracing.reporting.WavefrontSpanReporter;
import com.wavefront.sdk.appagent.jvm.reporter.WavefrontJvmReporter;
import com.wavefront.sdk.common.WavefrontSender;
import com.wavefront.sdk.common.application.ApplicationTags;
import com.wavefront.sdk.grpc.WavefrontServerTracerFactory;
import com.wavefront.sdk.grpc.reporter.WavefrontGrpcReporter;
import com.wfsample.beachshirts.Void;
import com.wfsample.beachshirts.*;
import com.wfsample.common.BeachShirtsUtils;
import com.wfsample.common.GrpcServiceConfig;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.apache.commons.lang3.BooleanUtils;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Driver for the packaging service which packs the a given shirt.
 *
 * @author Srujan Narkedamalli (snarkedamall@wavefront.com).
 */
public class PackagingService {

	public PackagingService(GrpcServiceConfig config) throws Exception {
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
				addService(new PackagingImpl()).addStreamTracerFactory(tracerFactory);
		Server packaging = builder.build();
		System.out.println("Starting Packaging server ...");
		packaging.start();
		System.out.println("Packaging server started");
		packaging.awaitTermination();
	}

	public static void main(String[] args) throws Exception {
		GrpcServiceConfig configuration = BeachShirtsUtils.scenarioFromFile(args[0]);
		new PackagingService(configuration);
	}

	static class PackagingImpl extends PackagingGrpc.PackagingImplBase {
		private final AtomicInteger getTypes = new AtomicInteger(0);
		private final AtomicInteger restock = new AtomicInteger(0);
		private final AtomicInteger wrap = new AtomicInteger(0);
		private final AtomicInteger giftWrap = new AtomicInteger(0);
		private final Random rand = new Random();

		@Override
		public void wrapShirts(WrapRequest request, StreamObserver<PackedShirts> responseObserver) {

			BeachShirtsUtils.consumeCpu(55, 0.3);

			// report error 2.5%
			if (rand.nextDouble() < 0.025) {
				responseObserver.onError(Status.INTERNAL.asRuntimeException());
			}

			responseObserver.onNext(PackedShirts.newBuilder().
					addAllShirts(request.getShirtsList()).
					build());
			responseObserver.onCompleted();
		}

		@Override
		public void giftWrap(WrapRequest request, StreamObserver<GiftPack> responseObserver) {

			// report error 3.5%
			if (rand.nextDouble() < 0.035) {
				responseObserver.onError(Status.INTERNAL.asRuntimeException());
			}

			BeachShirtsUtils.consumeCpu(70, 0.5);

			int resp = (int) Math.round(rand.nextDouble() * 100.0);
			if (request.getShirtsCount() > 40) {
				resp += 10000;
				BeachShirtsUtils.consumeCpu(7000, 0.1);
			}
			ByteString byteString = ByteString.copyFrom(new byte[resp]);
			responseObserver.onNext(GiftPack.newBuilder().setGiftMaterial(byteString).build());
			responseObserver.onCompleted();
		}

		@Override
		public void restockMaterial(WrappingType request,
		                            StreamObserver<com.wfsample.beachshirts.Status> responseObserver) {

			BeachShirtsUtils.consumeCpu(40, 0.3);

			// report error 7%
			if (rand.nextDouble() < 0.07) {
				responseObserver.onError(Status.INTERNAL.asRuntimeException());
			} else {
				responseObserver.onNext(com.wfsample.beachshirts.Status.newBuilder().
						setStatus(true).build());
				responseObserver.onCompleted();
			}
		}

		@Override
		public void getPackingTypes(Void request, StreamObserver<WrappingTypes> responseObserver) {

			BeachShirtsUtils.consumeCpu(60, 0.3);

			// report error 7%
			if (rand.nextDouble() < 0.07) {
				responseObserver.onError(Status.INTERNAL.asRuntimeException());
			} else {
				responseObserver.onNext(WrappingTypes.newBuilder().addWrappingType(WrappingType.
						newBuilder().setWrappingType("wrap wrap wrap wrap").build()).build());
				responseObserver.onCompleted();
			}
		}
	}
}