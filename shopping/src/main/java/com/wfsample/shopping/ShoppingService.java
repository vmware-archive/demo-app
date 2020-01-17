package com.wfsample.shopping;

import com.google.common.collect.ImmutableMap;
import com.wavefront.sdk.dropwizard.reporter.WavefrontDropwizardReporter;
import com.wavefront.sdk.jersey.WavefrontJerseyFactory;
import com.wfsample.common.BeachShirtsUtils;
import com.wfsample.common.DropwizardServiceConfig;
import com.wfsample.common.dto.DeliveryStatusDTO;
import com.wfsample.common.dto.OrderDTO;
import com.wfsample.common.dto.OrderStatusDTO;
import com.wfsample.common.dto.PackedShirtsDTO;
import com.wfsample.service.DeliveryApi;
import com.wfsample.service.InventoryApi;
import com.wfsample.service.PaymentsApi;
import com.wfsample.service.ShoppingApi;
import com.wfsample.service.StylingApi;
import io.dropwizard.Application;
import io.dropwizard.setup.Environment;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.log.Fields;
import io.opentracing.tag.Tags;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.ws.rs.core.Response;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Driver for Shopping service provides consumer facing APIs supporting activities like browsing
 * different styles of beachshirts, and ordering beachshirts.
 *
 * @author Srujan Narkedamalli (snarkedamall@wavefront.com).
 */
public class ShoppingService extends Application<DropwizardServiceConfig> {
	private DropwizardServiceConfig configuration;

	private ShoppingService() {
	}

	public static void main(String[] args) throws Exception {
		new ShoppingService().run(args);
	}

	@Override
	public void run(DropwizardServiceConfig configuration, Environment environment) {
		this.configuration = configuration;
		String inventoryUrl = "http://" + configuration.getInventoryHost() + ":" +
				configuration.getInventoryPort();
		String paymentsUrl = "http://" + configuration.getPaymentsHost() + ":" +
				configuration.getPaymentsPort();
		String stylingUrl = "http://" + configuration.getStylingHost() + ":" +
				configuration.getStylingPort();
		String deliveryUrl = "http://" + configuration.getDeliveryHost() + ":" +
				configuration.getDeliveryPort();
		WavefrontJerseyFactory factory = new WavefrontJerseyFactory(
				configuration.getApplicationTagsYamlFile(), configuration.getWfReportingConfigYamlFile());
		WavefrontDropwizardReporter dropwizardReporter = new WavefrontDropwizardReporter.Builder(
				environment.metrics(), factory.getApplicationTags()).
				withSource(factory.getSource()).
				reportingIntervalSeconds(30).
				build(factory.getWavefrontSender());
		dropwizardReporter.start();
		environment.jersey().register(factory.getWavefrontJerseyFilter());
		environment.jersey().register(new ShoppingWebResource(
				BeachShirtsUtils.createProxyClient(inventoryUrl, InventoryApi.class,
						factory.getWavefrontJaxrsClientFilter()),
				BeachShirtsUtils.createProxyClient(paymentsUrl, PaymentsApi.class,
						factory.getWavefrontJaxrsClientFilter()),
				BeachShirtsUtils.createProxyClient(stylingUrl, StylingApi.class,
						factory.getWavefrontJaxrsClientFilter()),
				BeachShirtsUtils.createProxyClient(deliveryUrl, DeliveryApi.class,
						factory.getWavefrontJaxrsClientFilter()), factory.getTracer()));
	}

	public class ShoppingWebResource implements ShoppingApi {
		private final InventoryApi inventoryApi;
		private final PaymentsApi paymentsApi;
		private final StylingApi stylingApi;
		private final DeliveryApi deliveryApi;
		private final Tracer tracer;
		private final Random rand = new Random();

		public ShoppingWebResource(InventoryApi inventoryApi, PaymentsApi paymentsApi,
		                           StylingApi stylingApi, DeliveryApi deliveryApi, Tracer tracer) {
			this.inventoryApi = inventoryApi;
			this.stylingApi = stylingApi;
			this.deliveryApi = deliveryApi;
			this.paymentsApi = paymentsApi;
			this.tracer = tracer;
		}

		@Override
		public Response getShoppingMenu() {

			BeachShirtsUtils.consumeCpu(20, 1);

			return Response.ok(stylingApi.getAllStyles()).build();
		}

		@Override
		public Response orderShirts(OrderDTO orderDTO) {

			try {
				BeachShirtsUtils.consumeCpu(30, 0.5);

				String orderNum = UUID.randomUUID().toString();

				Response inventoryResponse = inventoryApi.available(orderDTO.getStyleName());
				if (inventoryResponse.getStatus() >= 400) {
					return Response.status(inventoryResponse.getStatus()).entity("Items out of stock, " +
							"please try again later").build();
				}

				Response paymentResponse = paymentsApi.pay(orderNum, orderDTO.getPayment());
				if (paymentResponse.getStatus() >= 400) {
					return Response.status(paymentResponse.getStatus()).entity("Payment not successful, " +
							"please try again later").build();
				}

				PackedShirtsDTO packedShirts = stylingApi.makeShirts(
						orderDTO.getStyleName(), orderDTO.getQuantity());

				Response deliveryResponse = deliveryApi.dispatch(orderNum, packedShirts);
				DeliveryStatusDTO deliveryStatus = deliveryResponse.readEntity(DeliveryStatusDTO.class);

				return Response.status(deliveryResponse.getStatus()).entity(new OrderStatusDTO(orderNum,
						deliveryStatus.getStatus())).build();

			} catch (Exception e) {
				spanLogException(e);
				throw e;
			}
		}

		@Override
		public Response getOrderStatus() {

			BeachShirtsUtils.consumeCpu(30, 0.5);

			return deliveryApi.trackOrder("42");
		}

		@Override
		public Response cancelShirtsOrder() {

			BeachShirtsUtils.consumeCpu(100, 0.5);

			return deliveryApi.cancelOrder("42");
		}

		@Override
		public Response updateInventory() {

			BeachShirtsUtils.consumeCpu(40, 0.5);

			if (rand.nextDouble() < 0.333) {
				return stylingApi.addStyle("21");
			} else {
				return stylingApi.restockStyle("42");
			}
		}

		@Override
		public Response removeFromMenu(String id) {

			BeachShirtsUtils.consumeCpu(40, 0.5);

			if (rand.nextDouble() < 0.09) {
				spanLogException(new Exception("could not remove item from menu"));
			}
			return Response.ok().build();
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
