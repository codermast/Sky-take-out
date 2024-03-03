package com.codermast.sky.mapper;

import com.codermast.sky.dto.EmployeeDTO;
import com.codermast.sky.dto.EmployeePageQueryDTO;
import com.codermast.sky.entity.Employee;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface EmployeeMapper {

    // 根据用户名查询员工
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);

    // 插入员工数据
    @Insert("INSERT INTO employee ( name, username, password, phone, sex, id_number,  create_time, update_time, create_user, update_user,status) " +
            "values " +
            "(#{name},#{username},#{password},#{phone},#{sex},#{idNumber},#{createTime},#{updateTime},#{createUser},#{updateUser},#{status})")
    void insert(Employee employee);

    // 分页查询员工
    Page<Employee> pageQuery(EmployeePageQueryDTO employeePageQueryDTO);


    @Select("select * from employee where id = #{id}")
    EmployeeDTO getById(long id);
}
