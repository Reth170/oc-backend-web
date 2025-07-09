package jp.co.intellisea.oc.web.sales.service;

import jp.co.intellisea.oc.web.sales.entity.Contact;
import jp.co.intellisea.oc.web.sales.entity.Salary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface SalaryService {
    // 添加薪资记录
    Boolean addSalary(Salary salary);

    Boolean deleteSalary(long id);

    List<Salary> findByYearAndMonth(short year, short month);
}