package com.codermast.sky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.codermast.sky.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
