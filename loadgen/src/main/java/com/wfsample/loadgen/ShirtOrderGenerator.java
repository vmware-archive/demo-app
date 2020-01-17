package com.wfsample.loadgen;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * A simple script that invokes various APIs exposed by the beachshirts sample app to simulate a
 * simple load pattern of high number shirt requests periodically. This script needs host and port
 * on which sample app is running, and Requests per minute should be provided as input parameters.
 *
 * @author Srujan Narkedamalli (snarkedamall@wavefront.com).
 */
public class ShirtOrderGenerator {
	private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
	private static boolean debugLog = false;
	private static Random rand = new Random();

	public static void main(String[] args) {
		// args are: shoppingHost, shoppingPort, stylingHost, stylingPort, requestsPerMinute, numOfUsers, startCycle

		String debugLogEnv = System.getenv("DEBUG_LOG");
		if (debugLogEnv != null && debugLogEnv.equalsIgnoreCase("true")) {
			debugLog = true;
		}

		String shoppingHost = args[0];
		int shoppingPort = Integer.parseInt(args[1]);

		String stylingHost = args[2];
		int stylingPort = Integer.parseInt(args[3]);

		int requestsPerMinute = Integer.parseInt(args[4]);
		if (requestsPerMinute < 1) {
			System.out.println("please use requests per minute greater than or equal to 1");
			System.exit(1);
		}

		int loadUsers = Integer.parseInt(args[5]);
		if (loadUsers < 1) {
			System.out.println("please use # of users greater than or equal to 1");
			System.exit(1);
		}

		int startCycle = 0;
		if (args.length >= 7) {
			startCycle = Integer.parseInt(args[6]);
			if (startCycle < 0) {
				System.out.println("please use start cycle greater than or equal to 0");
				System.exit(1);
			}
		}

		System.out.println("Settings: ");
		System.out.printf("shopping host: %s\n", shoppingHost);
		System.out.printf("shopping port: %d\n", shoppingPort);
		System.out.printf("styling host: %s\n", stylingHost);
		System.out.printf("styling port: %d\n", stylingPort);
		System.out.printf("requests / min: %d\n", requestsPerMinute);
		System.out.printf("# of users: %d\n", loadUsers);
		System.out.printf("start cycle: %d\n", startCycle);
		System.out.println();

		for (int i = 0; i < loadUsers; i++) {

			String userName = String.format("user-%03d", (i + 1));
			final int startCycleFinal = startCycle;

			new Thread(() -> {
				System.out.printf("Starting load user: %s\n", userName);
				new ShirtOrderGenerator(shoppingHost, shoppingPort, stylingHost, stylingPort, requestsPerMinute, userName).start(startCycleFinal);
			}).start();
		}
	}


	private String shoppingHost;
	private int shoppingPort;
	private String stylingHost;
	private int stylingPort;
	private int requestsPerMinute;
	private String userName;

	public ShirtOrderGenerator(String shoppingHost, int shoppingPort, String stylingHost, int stylingPort, int requestsPerMinute, String userName) {
		this.shoppingHost = shoppingHost;
		this.shoppingPort = shoppingPort;
		this.stylingHost = stylingHost;
		this.stylingPort = stylingPort;
		this.requestsPerMinute = requestsPerMinute;
		this.userName = userName;
	}

	private void start(int startCycle) {

		OkHttpClient client = new OkHttpClient().newBuilder().readTimeout(30, TimeUnit.SECONDS).build();

		// each cycle is when we hit a num of requests per minute i.e. approx a minute
		long loadCycleCount = startCycle;
		long millisPerRequests = TimeUnit.MINUTES.toMillis(1) / requestsPerMinute;
		int requestsInCurrentMinute = 0;
		int numShirtsToOrder = 0;

		while (true) {

			long requestStartTime = System.currentTimeMillis();

			try {
				requestsInCurrentMinute++;
				if (requestsInCurrentMinute > requestsPerMinute) {
					loadCycleCount++;
					requestsInCurrentMinute = 1;
				}

				if (rand.nextDouble() < 0.5) {
					shopMenu(client, shoppingHost, shoppingPort);
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

				orderShirt(client, shoppingHost, shoppingPort, numShirtsToOrder);

				Thread.sleep(1000);

				if (rand.nextDouble() < 0.5) {
					inventoryUpdate(client, shoppingHost, shoppingPort);
					Thread.sleep(1000);
				}

				// cancel orders
				if (rand.nextDouble() < 0.1) {
					cancelOrder(client, shoppingHost, shoppingPort);
					Thread.sleep(1000);
				}

				// check status
				if (rand.nextDouble() < 0.25) {
					checkStatus(client, shoppingHost, shoppingPort);
				}

				if (rand.nextDouble() < 0.75) {
					deleteStyle(client, stylingHost, stylingPort);
				}

			} catch (java.net.SocketException | java.net.SocketTimeoutException e) {
				logError(String.format("**** %s ****", e.getMessage()));
			} catch (IOException | InterruptedException e) {
				logError(e.getMessage());
				e.printStackTrace();
			}

			try {
				long timeLeft = millisPerRequests - (System.currentTimeMillis() - requestStartTime);
				if (timeLeft <= 0) {
					logError("**** loadgen is time bound and is not meeting load targets ****");
				} else {
					Thread.sleep(timeLeft);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		} // main cycle loop
	}

	private void shopMenu(OkHttpClient client, String shoppingHost, int shoppingPort) throws IOException {
		HttpUrl url = new HttpUrl.Builder().scheme("http").host(shoppingHost).port(shoppingPort).
				addPathSegments("shop/menu")
				.build();
		Request.Builder requestBuilder = new Request.Builder().url(url);
		Request request = requestBuilder.build();
		Response response = client.newCall(request).execute();
		if (response.code() != 200) {
			logError("error getting shopping menu");
		} else {
			logDebug("successfully got menu");
		}
		response.close();
	}

	private void orderShirt(OkHttpClient client, String shoppingHost, int shoppingPort, int numShirtsToOrder) throws IOException {
		RequestBody json = RequestBody.create(JSON, "{\"styleName\" : \"foo\",\"quantity\" : " +
				"" + numShirtsToOrder + ",\"payment\" : {\"name\" : \"John Smith\",\"creditCardNum\" " +
				": \"0000111122223333\"}}");
		Request.Builder requestBuilder = new Request.Builder().url
				("http://" + shoppingHost + ":" + shoppingPort + "/shop/order").post(json);
		Request request = requestBuilder.build();
		Response response = client.newCall(request).execute();
		if (response.code() != 200) {
			logError("error ordering shirts");
		} else {
			logDebug("successfully ordered shirts");
		}
		response.close();
	}

	private void inventoryUpdate(OkHttpClient client, String shoppingHost, int shoppingPort) throws IOException {
		RequestBody json2 = RequestBody.create(JSON, "{}");
		Request.Builder requestBuilder = new Request.Builder().url
				("http://" + shoppingHost + ":" + shoppingPort + "/shop/inventory/update").post(json2);
		Request request = requestBuilder.build();
		Response response = client.newCall(request).execute();
		if (response.code() != 200) {
			logError("error updating inventory");
		} else {
			logDebug("updated inventory successfully");
		}
		response.close();
	}

	private void cancelOrder(OkHttpClient client, String shoppingHost, int shoppingPort) throws IOException {
		RequestBody json2 = RequestBody.create(JSON, "{}");
		Request.Builder requestBuilder = new Request.Builder().url
				("http://" + shoppingHost + ":" + shoppingPort + "/shop/cancel").post(json2);
		Request request = requestBuilder.build();
		Response response = client.newCall(request).execute();
		if (response.code() != 200) {
			logError("error cancelling order");
		} else {
			logDebug("cancelled order successfully");
		}
		response.close();
	}

	private void checkStatus(OkHttpClient client, String shoppingHost, int shoppingPort) throws IOException {
		HttpUrl url = new HttpUrl.Builder().scheme("http").host(shoppingHost).port(shoppingPort).
				addPathSegments("shop/status/1234").build();
		Request.Builder requestBuilder = new Request.Builder().url(url);
		Request request = requestBuilder.build();
		Response response = client.newCall(request).execute();
		if (response.code() != 200) {
			logError("error getting order status");
		} else {
			logDebug("successfully got order status");
		}
		response.close();
	}

	private void deleteStyle(OkHttpClient client, String stylingHost, int stylingPort) throws IOException {
		RequestBody json3 = RequestBody.create(JSON, "{}");
		Request.Builder requestBuilder = new Request.Builder().url
				("http://" + stylingHost + ":" + stylingPort + "/style/9/delete").post(json3);
		Request request = requestBuilder.build();
		Response response = client.newCall(request).execute();
		if (response.code() != 200) {
			logError("error deleting style");
		} else {
			logDebug("successfully deleted style");
		}
		response.close();
	}

	private void logDebug(String message) {
		if (debugLog) {
			log(message);
		}
	}

	private void logError(String message) {
		log(message);
	}

	private void log(String message) {
		String dstr = String.format("%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS.%1$tL", new Date());
		System.out.printf("%s %s: %s\n", dstr, userName, message);
	}

}
