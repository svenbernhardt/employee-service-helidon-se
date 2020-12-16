package de.sbernhardt.demo.employee;

import io.helidon.config.Config;
import io.helidon.health.HealthSupport;
import io.helidon.health.checks.HealthChecks;
import io.helidon.media.jackson.JacksonSupport;
import io.helidon.metrics.MetricsSupport;
import io.helidon.webserver.Routing;
import io.helidon.webserver.WebServer;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;

/**
 * The application main class.
 */
public final class Main {

  /**
   * Cannot be instantiated.
   */
  private Main() {
  }

  /**
   * Application main entry point.
   *
   * @param args command line arguments.
   * @throws IOException if there are problems reading logging properties
   */
  public static void main(final String[] args) throws IOException {
    startServer();
  }

  /**
   * Start the server.
   *
   * @return the created {@link WebServer} instance
   * @throws IOException if there are problems reading logging properties
   */
  static WebServer startServer() throws IOException {
    // load logging configuration
    setupLogging();

    // By default this will pick up application.yaml from the classpath
    Config config = Config.create();

    // Build server with JSONP support
    WebServer server = WebServer.builder(createRouting(config))
        .config(config.get("server"))
        .addMediaSupport(JacksonSupport.create())
        .build();

    // Try to start the server. If successful, print some info and arrange to
    // print a message at shutdown. If unsuccessful, print the exception.
    server.start()
        .thenAccept(ws -> {
          System.out.println(
              "WEB server is up! http://localhost:" + ws.port() + "/employees");
          ws.whenShutdown().thenRun(()
              -> System.out.println("WEB server is DOWN. Good bye!"));
        })
        .exceptionally(t -> {
          System.err.println("Startup failed: " + t.getMessage());
          t.printStackTrace(System.err);
          return null;
        });

    // Server threads are not daemon. No need to block. Just react.

    return server;
  }

  /**
   * Creates new {@link Routing}.
   *
   * @param config configuration of this server
   * @return routing configured with JSON support, a health check, and a service
   */
  private static Routing createRouting(Config config) {
    MetricsSupport metrics = MetricsSupport.create();
    EmployeeService lEmployeeService = new EmployeeService(config);
    HealthSupport health = HealthSupport.builder()
        .addLiveness(HealthChecks.healthChecks())   // Adds a convenient set of checks
        .build();

    return Routing.builder()
        .register(health)                   // Health at "/health"
        .register(metrics)                  // Metrics at "/metrics"
        .register("/employees", lEmployeeService)
        .build();
  }

  /**
   * Configure logging from logging.properties file.
   */
  private static void setupLogging() throws IOException {
    try (InputStream is = Main.class.getResourceAsStream("/logging.properties")) {
      LogManager.getLogManager().readConfiguration(is);
    }
  }
}
