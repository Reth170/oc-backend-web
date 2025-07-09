package jp.co.intellisea.oc.web.sales.controller;

import com.alibaba.fastjson.JSONObject;
import jp.co.intellisea.oc.web.sales.entity.Employee;
import jp.co.intellisea.oc.web.sales.entity.Salary;
import jp.co.intellisea.oc.web.sales.service.impl.EmployeeServiceImpl;
import jp.co.intellisea.oc.web.sales.service.impl.SalaryServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
public class SalaryControllerTest {

    @Autowired
    private SalaryController salaryController;

    @Autowired
    private SalaryServiceImpl salaryService;

    @Autowired
    private EmployeeServiceImpl employeeService;

    private final MockHttpServletRequest mockRequest = new MockHttpServletRequest();

    private Employee testEmployee;
    private Salary testSalary;

    @BeforeEach
    void setUp() {
        // 创建并保存测试员工
        testEmployee = new Employee();
        testEmployee.setEmployeeId(1001);
        testEmployee.setName("Test Employee");
        testEmployee.setMail("test@example.com");
        testEmployee.setEmployeeCode(10001);
        testEmployee.setDuty("Developer");
        employeeService.addEmployee(testEmployee);

        // 初始化测试薪资记录
        testSalary = new Salary();
        testSalary.setYear((short) 2023);
        testSalary.setMonth((short) 10);
        testSalary.setPaymentDate(LocalDate.of(2023, 10, 25));
        testSalary.setBaseSalary(250000);
        testSalary.setOvertimeAllowance(20000);
        testSalary.setCommutingAllowance(15000);
        testSalary.setHealthInsurance(12000);
        testSalary.setPension(10000);
        testSalary.setEmploymentInsurance(800);
        testSalary.setIncomeTax(15000);
        testSalary.setResidentTax(18000);
        testSalary.setEmployee(testEmployee);
    }

    @AfterEach
    void tearDown() {
        // 清理所有测试数据
        salaryService.findByYearAndMonth(testSalary.getYear(), testSalary.getMonth())
                .forEach(s -> salaryService.deleteSalary(s.getId()));

        employeeService.deleteEmployee(testEmployee.getEmployeeId());
    }

    @Test
    void testAddAndRetrieveSalary() {
        // 添加薪资记录
        JSONObject addResult = salaryController.addSalary(
                mockRequest,
                testEmployee.getEmployeeId(),
                testSalary.getYear(),
                testSalary.getMonth(),
                testSalary.getPaymentDate(),
                testSalary.getBaseSalary(),
                testSalary.getOvertimeAllowance(),
                testSalary.getCommutingAllowance(),
                testSalary.getHealthInsurance(),
                testSalary.getPension(),
                testSalary.getEmploymentInsurance(),
                testSalary.getIncomeTax(),
                testSalary.getResidentTax()
        );

        // 验证添加结果
        assertTrue(addResult.getBoolean("success"));
        assertEquals("adding successful!", addResult.getString("message"));

        // 查询薪资记录
        JSONObject queryResult = salaryController.searchContact(testSalary.getYear(), testSalary.getMonth());
        List<Salary> salaries = (List<Salary>) queryResult.get("data");

        // 验证查询结果
        assertFalse(salaries.isEmpty());
        Salary savedSalary = salaries.get(0);

        // 验证基本字段
        assertEquals(testSalary.getBaseSalary(), savedSalary.getBaseSalary());
        assertEquals(testSalary.getOvertimeAllowance(), savedSalary.getOvertimeAllowance());
        assertEquals(testSalary.getCommutingAllowance(), savedSalary.getCommutingAllowance());
        assertEquals(testSalary.getHealthInsurance(), savedSalary.getHealthInsurance());

        // 验证员工关联
        assertNotNull(savedSalary.getEmployee());
        assertEquals(testEmployee.getEmployeeId(), savedSalary.getEmployee().getEmployeeId());
    }

    @Test
    void testAddSalaryWithInvalidEmployee() {
        // 使用无效的员工ID
        JSONObject addResult = salaryController.addSalary(
                mockRequest,
                9999, // 无效员工ID
                testSalary.getYear(),
                testSalary.getMonth(),
                testSalary.getPaymentDate(),
                testSalary.getBaseSalary(),
                testSalary.getOvertimeAllowance(),
                testSalary.getCommutingAllowance(),
                testSalary.getHealthInsurance(),
                testSalary.getPension(),
                testSalary.getEmploymentInsurance(),
                testSalary.getIncomeTax(),
                testSalary.getResidentTax()
        );

        // 验证添加失败
        assertFalse(addResult.getBoolean("success"));
        assertEquals("adding error.", addResult.getString("message"));
    }

    @Test
    void testFindByYearAndMonthWithNoResults() {
        // 查询不存在的年月
        JSONObject queryResult = salaryController.searchContact((short) 2099, (short) 12);
        List<Salary> salaries = (List<Salary>) queryResult.get("data");

        // 验证无结果返回
        assertTrue(salaries.isEmpty());
    }

    @Test
    void testAddSalaryWithMinimalData() {
        // 使用最小数据集测试
        JSONObject addResult = salaryController.addSalary(
                mockRequest,
                testEmployee.getEmployeeId(),
                (short) 2023,
                (short) 11,
                LocalDate.of(2023, 11, 20),
                200000,
                0,
                0,
                10000,
                8000,
                500,
                10000,
                12000
        );

        assertTrue(addResult.getBoolean("success"));
        assertEquals("adding successful!", addResult.getString("message"));

        // 验证数据存储正确
        JSONObject queryResult = salaryController.searchContact((short) 2023, (short) 11);
        List<Salary> salaries = (List<Salary>) queryResult.get("data");
        assertEquals(1, salaries.size());

        Salary savedSalary = salaries.get(0);
        assertEquals(200000, savedSalary.getBaseSalary());
        assertEquals(0, savedSalary.getOvertimeAllowance());
        assertEquals(10000, savedSalary.getHealthInsurance());
    }
}