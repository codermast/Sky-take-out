package com.codermast.sky.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.codermast.sky.entity.Setmeal;
import com.codermast.sky.mapper.SetmealMapper;
import com.codermast.sky.service.SetmealService;
import org.springframework.stereotype.Service;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
}
