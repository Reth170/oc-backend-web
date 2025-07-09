package jp.co.intellisea.oc.web.sales.controller;

import com.alibaba.fastjson.JSONObject;
import jp.co.intellisea.oc.web.sales.common.ErrorMessage;
import jp.co.intellisea.oc.web.sales.common.SuccessMessage;
import jp.co.intellisea.oc.web.sales.entity.Employee;
import jp.co.intellisea.oc.web.sales.entity.Salary;
import jp.co.intellisea.oc.web.sales.service.impl.SalaryServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;

@RestController
public class SalaryController {
    @Autowired
    private SalaryServiceImpl salaryService;
    @ResponseBody
    @PostMapping("/salary/add") // 使用简化的PostMapping注解
    public JSONObject addSalary(HttpServletRequest req,
                                @RequestParam("employeeId") Integer employeeId, // 关联员工ID
                                @RequestParam("year") Short year,
                                @RequestParam("month") Short month,
                                @RequestParam("paymentDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate paymentDate,
                                @RequestParam("baseSalary") Integer baseSalary,
                                @RequestParam("overtimeAllowance") Integer overtimeAllowance,
                                @RequestParam("commutingAllowance") Integer commutingAllowance,
                                @RequestParam("healthInsurance") Integer healthInsurance,
                                @RequestParam("pension") Integer pension,
                                @RequestParam("employmentInsurance") Integer employmentInsurance,
                                @RequestParam("incomeTax") Integer incomeTax,
                                @RequestParam("residentTax") Integer residentTax) {

        // 1. 创建Salary对象并填充属性
        Salary salary = new Salary();
        salary.setYear(year);
        salary.setMonth(month);
        salary.setPaymentDate(paymentDate);
        salary.setBaseSalary(baseSalary);
        salary.setOvertimeAllowance(overtimeAllowance);
        salary.setCommutingAllowance(commutingAllowance);
        salary.setHealthInsurance(healthInsurance);
        salary.setPension(pension);
        salary.setEmploymentInsurance(employmentInsurance);
        salary.setIncomeTax(incomeTax);
        salary.setResidentTax(residentTax);

        // 2. 关联Employee对象（只需设置ID）
        Employee employee = new Employee();
        employee.setEmployeeId(employeeId);
        salary.setEmployee(employee); // 关联员工

        // 3. 调用Service层插入数据
        boolean res = salaryService.addSalary(salary);

        // 4. 返回标准化JSON响应
        if(res)
            return new SuccessMessage("adding successful!", true).getMessage();
        return new ErrorMessage("adding error.").getMessage();
    }

    @RequestMapping(value = "/salary/findByYAM", method = RequestMethod.GET)
    public JSONObject searchContact(@RequestParam short year, @RequestParam short month) {
        return new SuccessMessage<List<Salary>>(null, salaryService.findByYearAndMonth(year, month)).getMessage();
    }
}