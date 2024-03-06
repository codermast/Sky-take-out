package com.codermast.sky.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.codermast.sky.entity.Orders;
import com.codermast.sky.mapper.OrdersMapper;
import com.codermast.sky.service.OrdersService;
import org.springframework.stereotype.Service;

@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {
}
