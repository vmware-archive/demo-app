package com.wfsample.styling;

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
import com.wfsample.common.dto.ShirtDTO;
import com.wfsample.common.dto.ShirtStyleDTO;
import com.wfsample.service.InventoryApi;
import com.wfsample.service.ShoppingApi;
import com.wfsample.service.StylingApi;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.ws.rs.core.Response;

import io.dropwizard.Application;
import io.dropwizard.setup.Environment;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.tag.Tags;

/**
 * Driver for styling service which manages different styles of shirts and takes orders for a shirts
 * of a given style.
 *
 * @author Srujan Narkedamalli (snarkedamall@wavefront.com).
 */
public class StylingService extends Application<DropwizardServiceConfig> {
  private DropwizardServiceConfig configuration;

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
            factory.getWavefrontJaxrsClientFilter()), factory.getTracer(), interceptor));
  }

  public class StylingWebResource implements StylingApi {
    private final PrintingGrpc.PrintingBlockingStub printing;
    private final PackagingGrpc.PackagingBlockingStub packaging;
    private final InventoryApi inventoryApi;
    private final ShoppingApi shoppingApi;
    private final Tracer tracer;
    private final AtomicInteger counter = new AtomicInteger(0);
    // sample set of static styles.
    private List<ShirtStyleDTO> shirtStyleDTOS = new ArrayList<>();

    public StylingWebResource(InventoryApi inventoryApi, ShoppingApi shoppingApi,
                              Tracer tracer,
                              WavefrontClientInterceptor clientInterceptor) {
      this.inventoryApi = inventoryApi;
      this.shoppingApi = shoppingApi;
      this.tracer = tracer;
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

    @Override
    public List<ShirtStyleDTO> getAllStyles() {
      try {
        Thread.sleep(10);
        printing.getAvailableColors(Void.getDefaultInstance());
        Thread.sleep(10);
        packaging.getPackingTypes(Void.getDefaultInstance());
        Thread.sleep(10);
        return shirtStyleDTOS;
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

    @Override
    public PackedShirtsDTO makeShirts(String id, int quantity) {
      // create the record in database.
      Span jdbcSpan = tracer.buildSpan("createRecord").
              withTag(Tags.SPAN_KIND.getKey(), Tags.SPAN_KIND_CLIENT).
              withTag(Tags.COMPONENT.getKey(), "java-jdbc").
              withTag(Tags.DB_INSTANCE.getKey(), "stylingDB").
              withTag(Tags.DB_TYPE.getKey(), "postgresql").
              start();
      try (Scope jdbcScope = tracer.activateSpan(jdbcSpan)) {
        try {
          Thread.sleep(20);
          if (counter.incrementAndGet() % 110 == 0) {
            throw new RuntimeException();
          }
        } catch (Exception e) {
          Tags.ERROR.set(jdbcSpan, true);
          throw new RuntimeException(e);
        }
      } finally {
        jdbcSpan.finish();
      }
      try {
        Response checkoutResponse = inventoryApi.checkout(id);
        if (checkoutResponse.getStatus() >= 400) {
          throw new RuntimeException("unable to checkout resources from inventory");
        }
        Iterator<Shirt> shirts = printing.printShirts(PrintRequest.newBuilder().
            setStyleToPrint(ShirtStyle.newBuilder().setName(id).setImageUrl(id + "Image").build()).
            setQuantity(quantity).build());
        Thread.sleep(20);
        if (quantity < 30) {
          packaging.wrapShirts(WrapRequest.newBuilder().addAllShirts(() ->
              shirts).build());
        } else {
          packaging.giftWrap(WrapRequest.newBuilder().addAllShirts(() ->
              shirts).build());
        }
        Thread.sleep(20);
        List<ShirtDTO> packedShirts = new ArrayList<>(quantity);
        for (int i = 0; i < quantity; i++) {
          packedShirts.add(new ShirtDTO(new ShirtStyleDTO(id, id + "Image")));
        }
        return new PackedShirtsDTO(packedShirts);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

    @Override
    public Response addStyle(String id) {
      try {
        Thread.sleep(10);
        printing.addPrintColor(Color.newBuilder().setColor("rgb").build());
        Thread.sleep(10);
        return Response.ok().build();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

    @Override
    public Response restockStyle(String id) {
      try {
        Thread.sleep(10);
        printing.restockColor(Color.newBuilder().setColor("rgb").build());
        Thread.sleep(10);
        packaging.restockMaterial(WrappingType.newBuilder().setWrappingType("wrap").build());
        return Response.ok().build();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

    @Override
    public Response deleteStyle(String id) {
      try {
        Thread.sleep(10);
        shoppingApi.removeFromMenu(id);
        return Response.ok().build();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }
}
