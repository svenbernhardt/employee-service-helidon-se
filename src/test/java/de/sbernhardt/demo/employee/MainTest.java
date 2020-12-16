
package de.sbernhardt.demo.employee;

import com.fasterxml.jackson.databind.JsonNode;
import de.sbernhardt.demo.employee.model.Employee;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;

import io.helidon.media.jsonp.JsonpSupport;
import io.helidon.webclient.WebClient;
import io.helidon.webserver.WebServer;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class MainTest {

    private static WebServer webServer;
    private static WebClient webClient;

    @BeforeAll
    public static void startTheServer() throws Exception {
        webServer = Main.startServer();

        long timeout = 2000; // 2 seconds should be enough to start the server
        long now = System.currentTimeMillis();

        while (!webServer.isRunning()) {
            Thread.sleep(100);
            if ((System.currentTimeMillis() - now) > timeout) {
                Assertions.fail("Failed to start webserver");
            }
        }

        webClient = WebClient.builder()
                .baseUri("http://localhost:" + webServer.port())
                .addMediaSupport(JsonpSupport.create())
                .build();
    }

    @AfterAll
    public static void stopServer() throws Exception {
        if (webServer != null) {
            webServer.shutdown()
                    .toCompletableFuture()
                    .get(10, TimeUnit.SECONDS);
        }
    }


    public void testHelloWorld() throws Exception {
        webClient.get()
                .path("/employees")
                .request(JsonNode.class)
                .thenAccept(jsonObject -> Assertions.assertEquals("Hello World!", jsonObject.get("message")))
                .toCompletableFuture()
                .get();

        /*webClient.get()
                .path("/greet/Joe")
                .request(JsonObject.class)
                .thenAccept(jsonObject -> Assertions.assertEquals("Hello Joe!", jsonObject.getString("message")))
                .toCompletableFuture()
                .get();

        webClient.put()
                .path("/greet/greeting")
                .submit(TEST_JSON_OBJECT)
                .thenAccept(response -> Assertions.assertEquals(204, response.status().code()))
                .thenCompose(nothing -> webClient.get()
                        .path("/greet/Joe")
                        .request(JsonObject.class))
                .thenAccept(jsonObject -> Assertions.assertEquals("Hola Joe!", jsonObject.getString("message")))
                .toCompletableFuture()
                .get();*/

        webClient.get()
                .path("/health")
                .request()
                .thenAccept(response -> Assertions.assertEquals(200, response.status().code()))
                .toCompletableFuture()
                .get();

        webClient.get()
                .path("/metrics")
                .request()
                .thenAccept(response -> Assertions.assertEquals(200, response.status().code()))
                .toCompletableFuture()
                .get();
    }

}
