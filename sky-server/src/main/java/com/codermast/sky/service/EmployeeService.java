package com.codermast.sky.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.codermast.sky.dto.EmployeeLoginDTO;
import com.codermast.sky.entity.Employee;

public interface EmployeeService extends IService<Employee> {

    // 员工登录
    Employee login(EmployeeLoginDTO employeeLoginDTO);
}
