package de.sbernhardt.demo.employee.persistence;

import static com.google.common.base.Strings.isNullOrEmpty;

import com.google.common.base.Strings;
import de.sbernhardt.demo.employee.model.Employee;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class EmployeeDao {

  private Map<String, Employee> employees = new HashMap<>();

  public EmployeeDao() {

    final Employee employee = new Employee("GID120935", "Sven", "Bernhardt",
        "svenbernhardt@sttc.com",
        "Chief Architect", "male", "+(49) 2261 6001 0");

    employees.put(employee.getEmployeeNo(), employee);

  }

  public Employee save(Employee pEmployee) {

    //final String employeeNo = getUniqueEmployeeNo();

    employees.put(pEmployee.getEmployeeNo(), pEmployee);

    return pEmployee;
  }

  public Employee update(String pEmployeeNo, Employee pEmployee) {

    if (!employees.containsKey(pEmployeeNo)) {
      throw new IllegalArgumentException(
          String.format("Employee with EmployeeNo [%s] not found!", pEmployeeNo));
    }

    employees.put(pEmployeeNo, pEmployee);

    return pEmployee;
  }

  public void delete(String pEmployeeNo) {

    employees.remove(pEmployeeNo);
  }

  public List<Employee> find(String pLastName, String pEmployeeNo) {

    List<Employee> employeesResult = null;

    if (isNullOrEmpty(pLastName) && isNullOrEmpty(pEmployeeNo)) {
      employeesResult = List.copyOf(employees.values());
    } else if (isNullOrEmpty(pLastName)) {
      employeesResult = List.of(employees.get(pEmployeeNo));
    } else {
      employeesResult = Collections.emptyList();
    }

    return employeesResult;
  }

  private String getUniqueEmployeeNo() {
    String employeeNo = "";

    employeeNo = "GID"
        + Strings
        .padStart(String.valueOf(new Random().ints(1, 99999).limit(1).findFirst().getAsInt()), 5,
            '0');

    return employeeNo;
  }
}
