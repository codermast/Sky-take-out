package com.codermast.sky.controller.admin;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.codermast.sky.dto.OrdersPageQueryDTO;
import com.codermast.sky.entity.Orders;
import com.codermast.sky.result.PageResult;
import com.codermast.sky.result.Result;
import com.codermast.sky.service.OrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/admin/order")
public class OrderController {

    @Autowired
    private OrdersService ordersService;

    @GetMapping("/conditionSearch")
    public Result<PageResult> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO){
        int pageNum = ordersPageQueryDTO.getPage();
        int pageSize = ordersPageQueryDTO.getPageSize();
        String phone = ordersPageQueryDTO.getPhone();
        String number = ordersPageQueryDTO.getNumber();

        LocalDateTime beginTime = ordersPageQueryDTO.getBeginTime();
        LocalDateTime endTime = ordersPageQueryDTO.getEndTime();

        Page<Orders> page = new Page<>(pageNum,pageSize);

        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(phone != null,Orders::getPhone,phone);
        queryWrapper.eq(number != null,Orders::getNumber,number);
        queryWrapper.between(Orders::getOrderTime,beginTime,endTime);

        ordersService.page(page,queryWrapper);

        PageResult pageResult = new PageResult();
        pageResult.setRecords(page.getRecords());
        pageResult.setTotal(page.getTotal());

        return Result.success(pageResult);
    }
}
