package com.codermast.sky.service;

import com.codermast.sky.dto.EmployeeDTO;
import com.codermast.sky.dto.EmployeeLoginDTO;
import com.codermast.sky.entity.Employee;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    void save(EmployeeDTO employeeDTO);
}
