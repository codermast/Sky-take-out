package com.codermast.sky.service.impl;

import com.codermast.sky.constant.MessageConstant;
import com.codermast.sky.constant.PasswordConstant;
import com.codermast.sky.constant.StatusConstant;
import com.codermast.sky.context.BaseContext;
import com.codermast.sky.dto.EmployeeDTO;
import com.codermast.sky.dto.EmployeeLoginDTO;
import com.codermast.sky.dto.EmployeePageQueryDTO;
import com.codermast.sky.entity.Employee;
import com.codermast.sky.exception.AccountLockedException;
import com.codermast.sky.exception.AccountNotFoundException;
import com.codermast.sky.exception.PasswordErrorException;
import com.codermast.sky.mapper.EmployeeMapper;
import com.codermast.sky.result.PageResult;
import com.codermast.sky.service.EmployeeService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        // 对前端传过来的明文密码进行md5加密
        password = DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8));
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    @Override
    public void save(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        // 属性值复制
        BeanUtils.copyProperties(employeeDTO, employee);

        // 设置用户状态为可用
        employee.setStatus(StatusConstant.ENABLE);

        // 设置默认密码123456
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));

        // 设置当前记录的创建时间和修改时间
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());

        // 设置当前记录创建人 id 和修改人 id
        employee.setCreateUser(BaseContext.getCurrentId());
        employee.setUpdateUser(BaseContext.getCurrentId());

        // TODO 这里的 employee 对象的 id 是 null，没有自动获取
        employeeMapper.insert(employee);

    }

    @Override
    // 分页查询
    public PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO) {
        // 开始分页查询
        PageHelper.startPage(employeePageQueryDTO.getPage(), employeePageQueryDTO.getPageSize());

        Page<Employee> page = employeeMapper.pageQuery(employeePageQueryDTO);

        long total = page.getTotal();

        List<Employee> records = page.getResult();
        return new PageResult(total, records);
    }

    @Override
    // 根据员工 id 查员工信息
    public EmployeeDTO getById(long id) {
        EmployeeDTO employeeDTO = employeeMapper.getById(id);

        return employeeDTO;
    }

}
