package com.wfsample.loadgen;

import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A simple script that invokes various APIs exposed by the beachshirts sample app to simulate a
 * simple load pattern of high number shirt requests periodically. This script needs host and port
 * on which sample app is running, and Requests per minute should be provided as input parameters.
 *
 * @author Srujan Narkedamalli (snarkedamall@wavefront.com).
 */
public class ShirtOrderGenerator {
  private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

  public static void main(String[] args) {
    // args are: host, port, requests per second
    String host = args[0];
    int shoppingPort = Integer.parseInt(args[1]);
    int stylingPort = Integer.parseInt(args[2]);
    int requestsPerMinute = Integer.parseInt(args[3]);
    if (requestsPerMinute < 1) {
      System.out.println("please use requests per minute greater than or equal to 1");
      System.exit(1);
    }
    ShirtOrderGenerator loadgen = new ShirtOrderGenerator();
    loadgen.start(host, shoppingPort, stylingPort, requestsPerMinute);
  }

  void start(String host, int shoppingPort, int stylingPort, int requestsPerMinute) {
    OkHttpClient client = new OkHttpClient().newBuilder().readTimeout(2, TimeUnit.MINUTES).build();
    int requestsInCurrentMinute = 0;
    long start = System.currentTimeMillis();
    // each cycle is when we hit a num of requests per minute i.e. approx a minute
    long loadCycleCount = 0;
    int numShirtsToOrder = 0;
    while (true) {
      if (requestsInCurrentMinute >= requestsPerMinute) {
        loadCycleCount++;
        requestsInCurrentMinute = 0;
        long timeLeft = TimeUnit.MINUTES.toMillis(1) - (System.currentTimeMillis() - start);
        if (timeLeft <= 0) {
          System.out.println("loadgen is cpu bound and is not meeting target");
        } else {
          try {
            Thread.sleep(timeLeft);
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
        start = System.currentTimeMillis();
      }
      requestsInCurrentMinute++;
      if (loadCycleCount % 2 == 0) {
        try {
          HttpUrl url = new HttpUrl.Builder().scheme("http").host(host).port(shoppingPort).
              addPathSegments("shop/menu")
              .build();
          Request.Builder requestBuilder = new Request.Builder().url(url);
          Request request = requestBuilder.build();
          Response response = client.newCall(request).execute();
          if (response.code() != 200) {
            //throw new RuntimeException("Bad HTTP result: " + response);
            System.out.println("error getting shopping menu");
          } else {
            System.out.println("successfully got menu");
          }
          response.close();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
      if (loadCycleCount > 45 && loadCycleCount < 55) {
        // high number of shirts that can cause high latency
        numShirtsToOrder = 45;
      } else if (loadCycleCount >= 55) {
        // reset loadCycle to beginning to repeat pattern
        loadCycleCount = 0;
      } else if (loadCycleCount % 5 == 0) {
        // can cause errors
        numShirtsToOrder = 35;
      } else {
        numShirtsToOrder = 8;
      }
      // order shirts
      try {
        RequestBody json = RequestBody.create(JSON, "{\"styleName\" : \"foo\",\"quantity\" : " +
            "" + numShirtsToOrder + ",\"payment\" : {\"name\" : \"John Smith\",\"creditCardNum\" " +
            ": \"0000111122223333\"}}");
        Request.Builder requestBuilder = new Request.Builder().url
            ("http://" + host + ":" + shoppingPort + "/shop/order").post(json);
        Request request = requestBuilder.build();
        Response response = client.newCall(request).execute();
        if (response.code() != 200) {
          System.out.println("error ordering shirts");
        } else {
          System.out.println("successfully ordered shirts");
        }
        response.close();
        Thread.sleep(1000);
      } catch (Exception e) {
        System.out.println("error ordering shirts");
      }
      // inventory update
      if (loadCycleCount % 3 == 0) {
        try {
          RequestBody json2 = RequestBody.create(JSON, "{}");
          Request.Builder requestBuilder = new Request.Builder().url
              ("http://" + host + ":" + shoppingPort + "/shop/inventory/update").post(json2);
          Request request = requestBuilder.build();
          Response response = client.newCall(request).execute();
          if (response.code() != 200) {
            System.out.println("error updating inventory");
          } else {
            System.out.println("updated inventory successfully");
          }
          response.close();
          Thread.sleep(1000);

        } catch (Exception e) {
          e.printStackTrace();
          System.out.println("error updating inventory");
        }
      }
      // cancel orders
      if (loadCycleCount % 4 == 0) {
        try {
          RequestBody json2 = RequestBody.create(JSON, "{}");
          Request.Builder requestBuilder = new Request.Builder().url
              ("http://" + host + ":" + shoppingPort + "/shop/cancel").post(json2);
          Request request = requestBuilder.build();
          Response response = client.newCall(request).execute();
          if (response.code() != 200) {
            System.out.println("error cancelling order");
          } else {
            System.out.println("cancelled order successfully");
          }
          response.close();
          Thread.sleep(1000);

        } catch (Exception e) {
          e.printStackTrace();
          System.out.println("error updating inventory");
        }
      }
      // check status
      if (loadCycleCount % 5 == 0) {
        try {
          HttpUrl url = new HttpUrl.Builder().scheme("http").host(host).port(shoppingPort).
              addPathSegments("shop/status/1234").build();
          Request.Builder requestBuilder = new Request.Builder().url(url);
          Request request = requestBuilder.build();
          Response response = client.newCall(request).execute();
          if (response.code() != 200) {
            System.out.println("error getting order status");
          } else {
            System.out.println("successfully got order status");
          }
          response.close();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
      // delete style
      if (loadCycleCount % 6 == 0) {
        try {
          RequestBody json3 = RequestBody.create(JSON, "{}");
          Request.Builder requestBuilder = new Request.Builder().url
              ("http://" + host + ":" + stylingPort + "/style/9/delete").post(json3);
          Request request = requestBuilder.build();
          Response response = client.newCall(request).execute();
          if (response.code() != 200) {
            System.out.println("error deleting style");
          } else {
            System.out.println("successfully deleted style");
          }
          response.close();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
  }
}
