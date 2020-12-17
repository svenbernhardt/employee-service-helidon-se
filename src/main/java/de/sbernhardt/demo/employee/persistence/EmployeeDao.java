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

        final Employee obiwan = new Employee("GID000001", "Obiwan", "Kenobi", "obiwan.kenobi@rebels.com", "Jedi Master",
                "male", "+(002123) 213 123 312 312");

        final Employee luke = new Employee("GID000002", "Luke", "Skywalker", "luke.skywalker@rebels.com",
                "Jedi Padavan", "male", "+(002123) 213 123 312 312");

        final Employee han = new Employee("GID000003", "Han", "Solo", "han.solo@rebels.com", "General / Smuggler",
                "male", "+(002123) 213 123 312 312");

        employees.put(obiwan.getEmployeeNo(), obiwan);
        employees.put(luke.getEmployeeNo(), luke);
        employees.put(han.getEmployeeNo(), han);
    }

    public Employee save(Employee pEmployee) {

        // final String employeeNo = getUniqueEmployeeNo();

        employees.put(pEmployee.getEmployeeNo(), pEmployee);

        return pEmployee;
    }

    public Employee update(String pEmployeeNo, Employee pEmployee) {

        if (!employees.containsKey(pEmployeeNo)) {
            throw new IllegalArgumentException(String.format("Employee with EmployeeNo [%s] not found!", pEmployeeNo));
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
                + Strings.padStart(String.valueOf(new Random().ints(1, 99999).limit(1).findFirst().getAsInt()), 5, '0');

        return employeeNo;
    }
}
