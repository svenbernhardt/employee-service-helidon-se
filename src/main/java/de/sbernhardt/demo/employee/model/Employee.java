package de.sbernhardt.demo.employee.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"employeeNo", "firstName", "lastName", "gender", "jobTitle", "email", "phone"})
public class Employee {

  private String employeeNo = null;
  private String firstName = null;
  private String lastName = null;
  private String jobTitle = null;
  private String email = null;
  private String gender = null;
  private String phone = null;

  public Employee() {

  }

  public Employee(final String pEmployeeNo, final String pFirstName, final String pLastName,
      final String pJobTitle,
      final String pEmail, final String pGender, final String pPhone) {
    employeeNo = pEmployeeNo;
    firstName = pFirstName;
    lastName = pLastName;
    jobTitle = pJobTitle;
    email = pEmail;
    gender = pGender;
    phone = pPhone;
  }

  public String getEmployeeNo() {
    return employeeNo;
  }

  public void setEmployeeNo(final String pEmployeeNo) {
    employeeNo = pEmployeeNo;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(final String pFirstName) {
    firstName = pFirstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(final String pLastName) {
    lastName = pLastName;
  }

  public String getJobTitle() {
    return jobTitle;
  }

  public void setJobTitle(final String pJobTitle) {
    jobTitle = pJobTitle;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(final String pEmail) {
    email = pEmail;
  }

  public String getGender() {
    return gender;
  }

  public void setGender(final String pGender) {
    gender = pGender;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(final String pPhone) {
    phone = pPhone;
  }
}
