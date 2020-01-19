package com.test.crud.service;

import com.test.crud.bean.Employee;
import com.test.crud.bean.EmployeeExample;
import com.test.crud.dao.EmployeeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
@Service
public class EmployeeService {
    @Autowired
    EmployeeMapper employeeMapper;
    //查询所有员工
    public List<Employee> getAll() {
        return employeeMapper.selectByExampleWithDept(null);
    }
    //员工保存
    public void saveEmp(Employee employee) {
        employeeMapper.insertSelective(employee);
    }
    //检验用户名是否可用
    public boolean checkUser(String empName) {
        EmployeeExample example = new EmployeeExample();
        EmployeeExample.Criteria criteria =  example.createCriteria();
        criteria.andEmailEqualTo(empName);
        long conunt = employeeMapper.countByExample(example);
        return conunt == 0;
    }

    //按照员工ID查询员工
    public Employee getEmp(Integer id) {
        Employee employee = employeeMapper.selectByPrimaryKey(id);
        return employee;
    }

    //员工更新
    public void updateEmp(Employee employee) {
        employeeMapper.updateByPrimaryKeySelective(employee);

    }

    //员工删除
    public void deleteEmp(Integer id) {
        employeeMapper.deleteByPrimaryKey(id);
    }

    public void deleteBatch(List<Integer> ids) {
        EmployeeExample example = new EmployeeExample();
        EmployeeExample.Criteria criteria = example.createCriteria();
        //delete form xxx where emp_id in (1,2,3)
        criteria.andEmpIdIn(ids);
        employeeMapper.deleteByExample(example);
    }
}
