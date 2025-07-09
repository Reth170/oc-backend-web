package jp.co.intellisea.oc.web.sales.service.impl;

import jp.co.intellisea.oc.web.sales.dao.SalaryMapper;
import jp.co.intellisea.oc.web.sales.entity.Salary;
import jp.co.intellisea.oc.web.sales.service.SalaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SalaryServiceImpl implements SalaryService {

    @Autowired
    private SalaryMapper salaryMapper;

    @Override
    public Boolean addSalary(Salary salary) {return salaryMapper.insert(salary) > 0;}

    @Override
    public Boolean deleteSalary(long id) {return salaryMapper.delete(id) > 0;}

    @Override
    public List<Salary> findByYearAndMonth(short year, short month) {
        return salaryMapper.findByYearAndMonth(year, month);
    }
}
