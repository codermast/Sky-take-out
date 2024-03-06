package com.codermast.sky.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.codermast.sky.constant.JwtClaimsConstant;
import com.codermast.sky.constant.PasswordConstant;
import com.codermast.sky.context.BaseContext;
import com.codermast.sky.dto.EmployeeDTO;
import com.codermast.sky.dto.EmployeeLoginDTO;
import com.codermast.sky.dto.EmployeePageQueryDTO;
import com.codermast.sky.entity.Employee;
import com.codermast.sky.properties.JwtProperties;
import com.codermast.sky.result.PageResult;
import com.codermast.sky.result.Result;
import com.codermast.sky.service.EmployeeService;
import com.codermast.sky.utils.JwtUtil;
import com.codermast.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Api(tags = "员工管理")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private JwtProperties jwtProperties;

    // 登录
    @ApiOperation("登录")
    @PostMapping("/login")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    // 退出登录
    @ApiOperation("退出登录")
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success();
    }

    //新增员工
    @ApiOperation("注册")
    @PostMapping
    public Result<EmployeeDTO> save(@RequestBody EmployeeDTO employeeDTO) {
        log.info("新增员工：{}", employeeDTO);

        Employee employee = new Employee();

        BeanUtils.copyProperties(employeeDTO, employee);

        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));
        employee.setUpdateUser(BaseContext.getCurrentId());
        employee.setCreateUser(BaseContext.getCurrentId());
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());

        employeeService.save(employee);

        return Result.success(employeeDTO);
    }

    // 员工分页查询
    @ApiOperation("分页查询")
    @GetMapping("/page")
    public Result<PageResult> page(EmployeePageQueryDTO employeePageQueryDTO) {
        log.info("员工分页查询，参数为：{}", employeePageQueryDTO);

        String name = employeePageQueryDTO.getName();
        int pageNum = employeePageQueryDTO.getPage();
        int pageSize = employeePageQueryDTO.getPageSize();

        // 开始分页查询
        Page<Employee> page = new Page<>(pageNum, pageSize);

        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Employee::getId);
        queryWrapper.like(name != null, Employee::getName, name);

        employeeService.page(page, queryWrapper);

        PageResult pageResult = new PageResult();
        pageResult.setTotal(page.getTotal());
        pageResult.setRecords(page.getRecords());

        return Result.success(pageResult);
    }

    // 根据 id 查员工
    @GetMapping("/{id}")
    public Result<EmployeeDTO> queryById(@PathVariable long id) {
        log.info("员工的 ID 为：{}", id);

        Employee employee = employeeService.getById(id);

        EmployeeDTO employeeDTO = new EmployeeDTO();

        BeanUtils.copyProperties(employee, employeeDTO);

        return Result.success(employeeDTO);
    }

    // 更新员工信息
    @PutMapping
    public Result<EmployeeDTO> update(@RequestBody EmployeeDTO employeeDTO) {
        log.info("更新后的员工信息：{}", employeeDTO);
        Employee employee = new Employee();

        BeanUtils.copyProperties(employeeDTO, employee);

        QueryWrapper<Employee> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", employee.getId());

        boolean ret = employeeService.update(employee, queryWrapper);

        if (ret) {
            return Result.success(employeeDTO);
        } else {
            return Result.error("更新失败！");
        }
    }

    // 更改员工状态
    @PostMapping("/status/{status}")
    public Result status(@PathVariable int status, Employee employee) {
        employee.setStatus(status);

        employeeService.updateById(employee);

        return Result.success();
    }
}
