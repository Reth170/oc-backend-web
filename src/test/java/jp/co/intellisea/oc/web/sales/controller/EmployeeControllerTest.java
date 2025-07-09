package jp.co.intellisea.oc.web.sales.controller;

import com.alibaba.fastjson.JSONObject;
import jp.co.intellisea.oc.web.sales.entity.Employee;
import jp.co.intellisea.oc.web.sales.service.impl.EmployeeServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
public class EmployeeControllerTest {
    @Autowired
    private EmployeeServiceImpl employeeService;

    @Autowired
    private EmployeeController employeeController;

    private final MockHttpServletRequest mockRequest = new MockHttpServletRequest();

    @AfterEach
    void tearDown() {
        // 清理测试数据（事务回滚可替代）
        employeeService.allEmployee().forEach(e ->
                employeeService.deleteEmployee(e.getEmployeeId())
        );
    }
    @Test
    void testAddAndRetrieveEmployee() {
        // 准备测试数据
        Integer employeeId = 1001;
        String name = "Test User";
        String mail = "test@example.com";
        Integer employeeCode = 10001;
        String duty = "Developer";

        JSONObject addResult = employeeController.addEmployee(
                mockRequest,
                employeeId,
                name,
                mail,
                employeeCode,
                duty
        );

        // 验证添加结果
        assertTrue(addResult.getBoolean("success"));
        assertEquals("adding successful!", addResult.getString("message"));

        // 调用查询接口
        JSONObject queryResult = employeeController.allEmployee();
        List<Employee> employees = (List<Employee>) queryResult.get("data");
        assertFalse(employees.isEmpty());
        Employee savedEmployee = employees.stream()
                .filter(e -> e.getEmployeeId().equals(employeeId))
                .findFirst()
                .orElse(null);

        assertNotNull(savedEmployee);
        assertEquals(name, savedEmployee.getName());
        assertEquals(mail, savedEmployee.getMail());
    }

    @Test
    void testAddAndDeleteEmployee() {
        Integer employeeId = 1002;
        String name = "Delete Test";


        employeeController.addEmployee(
                mockRequest,
                employeeId,
                name,
                "delete@test.com",
                10002,
                "Tester"
        );

        // 删除员工
        JSONObject deleteResult = employeeController.deleteEmployee(mockRequest, employeeId);

        // 验证删除结果
        assertTrue(deleteResult.getBoolean("success"));
        assertEquals("delete employee successful!", deleteResult.getString("message"));

        // 验证数据库已删除
        Employee deleted = employeeService.findEmployeeById(employeeId);
        assertNull(deleted);
    }
}
