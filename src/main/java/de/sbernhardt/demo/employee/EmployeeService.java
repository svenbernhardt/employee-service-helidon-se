package de.sbernhardt.demo.employee;

import de.sbernhardt.demo.employee.model.Employee;
import de.sbernhardt.demo.employee.persistence.EmployeeDao;
import io.helidon.common.http.Http;
import io.helidon.common.http.Http.Status;
import io.helidon.config.Config;
import io.helidon.webserver.Routing;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;
import io.helidon.webserver.Service;
import java.net.URI;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.JsonException;

public class EmployeeService implements Service {

  private static final Logger LOGGER = Logger.getLogger(EmployeeService.class.getName());

  private final EmployeeDao employeeDao;

  EmployeeService(Config config) {
    employeeDao = new EmployeeDao();
  }

  @Override
  public void update(Routing.Rules rules) {
    rules
        .get("/", this::getEmployeesHandler)
        .post("/", this::createEmployeeHandler)
        .put("/{employeeNo}", this::updateEmployeeHandler)
        .delete("/{employeeNo}", this::deleteEmployeeHandler);
  }

  private void deleteEmployeeHandler(final ServerRequest pServerRequest,
      final ServerResponse pServerResponse) {

    final String lEmployeeNo = pServerRequest.path().param("employeeNo");

    LOGGER.info(String.format("Employee with employeeNo %s is getting deleted!", lEmployeeNo));

    employeeDao.delete(lEmployeeNo);
    pServerResponse.status(Http.Status.NO_CONTENT_204).send();
  }

  private void createEmployeeHandler(final ServerRequest pServerRequest,
      final ServerResponse pServerResponse) {

    pServerRequest.content().as(Employee.class)
        .thenApply(employee -> employeeDao.save(employee)).thenCompose(employee -> {

      LOGGER.info(String
          .format("New employee with employeeNo %s has been created!", employee.getEmployeeNo()));
      pServerResponse.status(Status.CREATED_201).headers()
          .location(URI.create(String.format("/employees/%s", employee.getEmployeeNo())));
      return pServerResponse.send(employee);
    }).exceptionally(ex -> processErrors(ex, pServerRequest, pServerResponse));
  }

  private void getEmployeesHandler(final ServerRequest pServerRequest,
      final ServerResponse pServerResponse) {

    //TODO: Query Param handling...

    LOGGER.info(String.format("Search for all employees!"));

    final List<Employee> lEmployees = employeeDao
        .find(null, null);
    pServerResponse.status(Status.OK_200).send(lEmployees);
  }

  private void updateEmployeeHandler(ServerRequest pServerRequest,
      ServerResponse pServerResponse) {
    pServerRequest.content().as(Employee.class)
        .thenApply(employee -> employeeDao.update(employee.getEmployeeNo(), employee))
        .thenCompose(employee -> {
          LOGGER.info(String
              .format("Existing employee with employeeNo %s has been updated!",
                  employee.getEmployeeNo()));
          pServerResponse.status(Status.OK_200).headers()
              .location(URI.create(String.format("/employees/%s", employee.getEmployeeNo())));
          return pServerResponse.send(employee);
        })
        .exceptionally(ex -> processErrors(ex, pServerRequest, pServerResponse));
  }

  private static <T> T processErrors(Throwable ex, ServerRequest request, ServerResponse response) {

    if (ex.getCause() instanceof JsonException) {

      LOGGER.log(Level.FINE, "Invalid JSON", ex);
      response.status(Http.Status.BAD_REQUEST_400).send("jsonErrorObject");
    } else {

      LOGGER.log(Level.FINE, "Internal error", ex);
      response.status(Http.Status.INTERNAL_SERVER_ERROR_500).send("jsonErrorObject");
    }

    return null;
  }
}