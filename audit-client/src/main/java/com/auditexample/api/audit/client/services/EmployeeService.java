package com.auditexample.api.audit.client.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmployeeService {

  public Integer getEmployeeId() {
    Integer employeeId = null;
    try {
      var authUserHolderClass = Class.forName("com.auditexample.auth.mvc.AuthUserHolder");
      var getEmployeeIdStaticMethod = authUserHolderClass.getDeclaredMethod("getEmployeeId");
      var result = (Integer) getEmployeeIdStaticMethod.invoke(null);
      if (result != -1) {
        employeeId = result;
      }
    } catch (Exception ex) {
      log.debug("Could not obtain employeeId from AuthUserHolder", ex);
    }
    return employeeId;
  }
}
