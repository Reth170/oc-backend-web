package jp.co.intellisea.oc.web.sales.service;

import jp.co.intellisea.oc.web.sales.entity.Employee;
import jp.co.intellisea.oc.web.sales.service.impl.EmployeeServiceImpl;

import javax.annotation.Resource;
import java.util.List;

public interface EmployeeService {
    boolean addEmployee(Employee employee);

    boolean deleteEmployee(int id);

    List<Employee> allEmployee();

    Employee findEmployeeById(int id);
}

