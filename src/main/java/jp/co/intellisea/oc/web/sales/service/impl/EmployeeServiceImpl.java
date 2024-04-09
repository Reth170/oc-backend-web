package jp.co.intellisea.oc.web.sales.service.impl;

import jp.co.intellisea.oc.web.sales.dao.EmployeeMapper;
import jp.co.intellisea.oc.web.sales.entity.Employee;
import jp.co.intellisea.oc.web.sales.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service

public class EmployeeServiceImpl implements EmployeeService {
    @Autowired
    private EmployeeMapper employeeMapper;
    @Override
    public boolean addEmployee(Employee employee) { return employeeMapper.insert(employee) > 0; }

    @Override
    public boolean deleteEmployee(int id) { return employeeMapper.deleteByPrimaryKey(id) > 0; }


}
