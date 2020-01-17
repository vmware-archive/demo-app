package com.wfsample.styling;

import com.google.common.collect.ImmutableMap;
import com.wavefront.sdk.dropwizard.reporter.WavefrontDropwizardReporter;
import com.wavefront.sdk.grpc.WavefrontClientInterceptor;
import com.wavefront.sdk.grpc.reporter.WavefrontGrpcReporter;
import com.wavefront.sdk.jersey.WavefrontJerseyFactory;
import com.wfsample.beachshirts.Color;
import com.wfsample.beachshirts.PackagingGrpc;
import com.wfsample.beachshirts.PrintRequest;
import com.wfsample.beachshirts.PrintingGrpc;
import com.wfsample.beachshirts.Shirt;
import com.wfsample.beachshirts.ShirtStyle;
import com.wfsample.beachshirts.Void;
import com.wfsample.beachshirts.WrapRequest;
import com.wfsample.beachshirts.WrappingType;
import com.wfsample.common.BeachShirtsUtils;
import com.wfsample.common.DropwizardServiceConfig;
import com.wfsample.common.dto.PackedShirtsDTO;
import com.wfsample.common.dto.ShirtStyleDTO;
import com.wfsample.service.InventoryApi;
import com.wfsample.service.ShoppingApi;
import com.wfsample.service.StylingApi;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.core.Response;

import io.dropwizard.Application;
import io.dropwizard.setup.Environment;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.log.Fields;
import io.opentracing.tag.Tags;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * Driver for styling service which manages different styles of shirts and takes orders for a shirts
 * of a given style.
 *
 * @author Srujan Narkedamalli (snarkedamall@wavefront.com).
 */
public class StylingService extends Application<DropwizardServiceConfig> {
	private DropwizardServiceConfig configuration;
	private Tracer tracer;

	private StylingService() {
	}

	public static void main(String[] args) throws Exception {
		new StylingService().run(args);
	}

	@Override
	public void run(DropwizardServiceConfig configuration, Environment environment) {
		this.configuration = configuration;
		String inventoryUrl = "http://" + configuration.getInventoryHost() + ":" +
				configuration.getInventoryPort();
		String shoppingUrl = "http://" + configuration.getShoppingHost() + ":" +
				configuration.getShoppingPort();
		WavefrontJerseyFactory factory = new WavefrontJerseyFactory(
				configuration.getApplicationTagsYamlFile(), configuration.getWfReportingConfigYamlFile());
		tracer = factory.getTracer();
		WavefrontDropwizardReporter dropwizardReporter = new WavefrontDropwizardReporter.Builder(
				environment.metrics(), factory.getApplicationTags()).
				withSource(factory.getSource()).
				reportingIntervalSeconds(30).
				build(factory.getWavefrontSender());
		dropwizardReporter.start();
		WavefrontGrpcReporter grpcReporter = new WavefrontGrpcReporter.Builder(
				factory.getApplicationTags()).
				withSource(factory.getSource()).
				reportingIntervalSeconds(30).
				build(factory.getWavefrontSender());
		grpcReporter.start();
		WavefrontClientInterceptor interceptor =
				new WavefrontClientInterceptor.Builder(grpcReporter, factory.getApplicationTags()).
						withTracer(factory.getTracer()).recordStreamingStats().build();
		environment.jersey().register(factory.getWavefrontJerseyFilter());
		environment.jersey().register(new StylingWebResource(
				BeachShirtsUtils.createProxyClient(inventoryUrl, InventoryApi.class,
						factory.getWavefrontJaxrsClientFilter()),
				BeachShirtsUtils.createProxyClient(shoppingUrl, ShoppingApi.class,
						factory.getWavefrontJaxrsClientFilter()), interceptor));
	}

	public class StylingWebResource implements StylingApi {
		private final PrintingGrpc.PrintingBlockingStub printing;
		private final PackagingGrpc.PackagingBlockingStub packaging;
		private final InventoryApi inventoryApi;
		private final ShoppingApi shoppingApi;
		// sample set of static styles.
		private List<ShirtStyleDTO> shirtStyleDTOS = new ArrayList<>();

		public StylingWebResource(InventoryApi inventoryApi, ShoppingApi shoppingApi,
		                          WavefrontClientInterceptor clientInterceptor) {
			this.inventoryApi = inventoryApi;
			this.shoppingApi = shoppingApi;
			ShirtStyleDTO dto = new ShirtStyleDTO();
			dto.setName("style1");
			dto.setImageUrl("style1Image");
			ShirtStyleDTO dto2 = new ShirtStyleDTO();
			dto2.setName("style2");
			dto2.setImageUrl("style2Image");
			shirtStyleDTOS.add(dto);
			shirtStyleDTOS.add(dto2);
			ManagedChannel printingChannel = ManagedChannelBuilder.forAddress(
					configuration.getPrintingHost(), configuration.getPrintingPort()).
					intercept(clientInterceptor).
					usePlaintext().build();
			ManagedChannel packagingChannel = ManagedChannelBuilder.forAddress(
					configuration.getPackagingHost(), configuration.getPackagingPort()).
					intercept(clientInterceptor).
					usePlaintext().build();
			printing = PrintingGrpc.newBlockingStub(printingChannel);
			packaging = PackagingGrpc.newBlockingStub(packagingChannel);
		}

		public List<ShirtStyleDTO> getAllStyles() {
			try {
				BeachShirtsUtils.consumeCpu(10, 0.3);
				printing.getAvailableColors(Void.getDefaultInstance());
				BeachShirtsUtils.consumeCpu(10, 0.3);
				packaging.getPackingTypes(Void.getDefaultInstance());
				BeachShirtsUtils.consumeCpu(10, 0.5);
				return shirtStyleDTOS;
			} catch (Exception e) {
				spanLogException(e);
				throw new RuntimeException(e);
			}
		}

		public PackedShirtsDTO makeShirts(String id, int quantity) {
			try {
				BeachShirtsUtils.consumeCpu(20, 0.5);
				Response checkoutResponse = inventoryApi.checkout(id);
				if (checkoutResponse.getStatus() >= 400) {
					throw new RuntimeException("unable to checkout resources from inventory");
				}
				Iterator<Shirt> shirts = printing.printShirts(PrintRequest.newBuilder().
						setStyleToPrint(ShirtStyle.newBuilder().setName(id).setImageUrl(id + "Image").build()).
						setQuantity(quantity).build());
				BeachShirtsUtils.consumeCpu(20, 0.5);
				if (quantity < 30) {
					packaging.wrapShirts(WrapRequest.newBuilder().addAllShirts(() ->
							shirts).build());
				} else {
					packaging.giftWrap(WrapRequest.newBuilder().addAllShirts(() ->
							shirts).build());
				}
				BeachShirtsUtils.consumeCpu(20, 1);
				return new PackedShirtsDTO(new ArrayList<>());
			} catch (Exception e) {
				spanLogException(e);
				throw new RuntimeException(e);
			}
		}

		@Override
		public Response addStyle(String id) {
			try {
				BeachShirtsUtils.consumeCpu(10, 0.3);
				printing.addPrintColor(Color.newBuilder().setColor("rgb").build());
				BeachShirtsUtils.consumeCpu(10, 0.5);
				return Response.ok().build();
			} catch (Exception e) {
				spanLogException(e);
				throw new RuntimeException(e);
			}
		}

		@Override
		public Response restockStyle(String id) {
			try {
				BeachShirtsUtils.consumeCpu(10, 0.3);
				printing.restockColor(Color.newBuilder().setColor("rgb").build());
				BeachShirtsUtils.consumeCpu(10, 0.5);
				packaging.restockMaterial(WrappingType.newBuilder().setWrappingType("wrap").build());
				return Response.ok().build();
			} catch (Exception e) {
				spanLogException(e);
				throw new RuntimeException(e);
			}
		}

		@Override
		public Response deleteStyle(String id) {
			try {
				BeachShirtsUtils.consumeCpu(10, 0.3);
				shoppingApi.removeFromMenu(id);
				return Response.ok().build();
			} catch (Exception e) {
				spanLogException(e);
				throw new RuntimeException(e);
			}
		}

		private void spanLogException(Exception e) {
			Span span = tracer.activeSpan();
			if (span != null) {
				Tags.ERROR.set(span, true);
				span.log(ImmutableMap.of(
						Fields.EVENT, "error",
						Fields.ERROR_KIND, e.getClass().getName(),
						Fields.STACK, ExceptionUtils.getStackTrace(e)
				));
			}
		}

	}
}
