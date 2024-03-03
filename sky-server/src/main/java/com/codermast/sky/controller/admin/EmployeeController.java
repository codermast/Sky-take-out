package com.codermast.sky.controller.admin;

import com.codermast.sky.dto.EmployeeDTO;
import com.codermast.sky.dto.EmployeePageQueryDTO;
import com.codermast.sky.result.PageResult;
import com.codermast.sky.service.EmployeeService;
import com.codermast.sky.constant.JwtClaimsConstant;
import com.codermast.sky.dto.EmployeeLoginDTO;
import com.codermast.sky.entity.Employee;
import com.codermast.sky.properties.JwtProperties;
import com.codermast.sky.result.Result;
import com.codermast.sky.utils.JwtUtil;
import com.codermast.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
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

    /**
     * 退出
     *
     * @return
     */
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
        employeeService.save(employeeDTO);

        return Result.success(employeeDTO);
    }

    // 员工分页查询
    @GetMapping("/page")
    public Result<PageResult> page(EmployeePageQueryDTO employeePageQueryDTO) {
        log.info("员工分页查询，参数为：{}", employeePageQueryDTO);

        PageResult pageResult =  employeeService.pageQuery(employeePageQueryDTO);
        return Result.success(pageResult);
    }

    // 根据 id 查员工
    @GetMapping("/{id}")
    public Result<EmployeeDTO> queryById(@PathVariable long id){
        log.info("员工的 ID 为：{}",id);

        EmployeeDTO employeeDTO =  employeeService.getById(id);
        return Result.success(employeeDTO);
    }


}
