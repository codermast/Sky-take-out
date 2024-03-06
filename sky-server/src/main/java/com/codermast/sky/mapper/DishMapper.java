package com.codermast.sky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.codermast.sky.entity.Dish;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
