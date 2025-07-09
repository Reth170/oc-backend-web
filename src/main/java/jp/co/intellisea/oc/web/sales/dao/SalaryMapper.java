package jp.co.intellisea.oc.web.sales.dao;

import jp.co.intellisea.oc.web.sales.entity.Salary;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface SalaryMapper {

    // 插入薪资记录（使用数据库默认值）
    int insert(Salary salary);

    // 根据ID查询薪资明细
    Optional<Salary> findById(Long id);

    // 更新薪资记录（排除不可变字段）
    int update(Salary salary);

    // 删除薪资记录
    int delete(Long id);

    // 根据员工ID查询薪资列表
    List<Salary> findByEmployeeId(Integer employeeId);

    // 根据年月查询薪资列表
    List<Salary> findByYearAndMonth(@Param("year") Short year, @Param("month") Short month);

    // 检查员工年月组合的唯一性
    boolean existsByEmployeeAndPeriod(
            @Param("employeeId") Integer employeeId,
            @Param("year") Short year,
            @Param("month") Short month
    );

    // 批量插入薪资记录
    int batchInsert(@Param("salaries") List<Salary> salaries);
}