package com.codermast.sky.controller.admin;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.codermast.sky.context.BaseContext;
import com.codermast.sky.dto.SetmealDTO;
import com.codermast.sky.dto.SetmealPageQueryDTO;
import com.codermast.sky.entity.Setmeal;
import com.codermast.sky.entity.SetmealDish;
import com.codermast.sky.mapper.SetmealMapper;
import com.codermast.sky.result.PageResult;
import com.codermast.sky.result.Result;
import com.codermast.sky.service.CategoryService;
import com.codermast.sky.service.SetmealDishService;
import com.codermast.sky.service.SetmealService;
import com.codermast.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/admin/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/page")
    public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO) {
        Integer pageNum = setmealPageQueryDTO.getPage();
        Integer pageSize = setmealPageQueryDTO.getPageSize();
        String name = setmealPageQueryDTO.getName();
        Integer categoryId = setmealPageQueryDTO.getCategoryId();
        Integer status = setmealPageQueryDTO.getStatus();

        Page<Setmeal> page = new Page<>(pageNum, pageSize);

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.like(name != null, Setmeal::getName, name);
        queryWrapper.eq(categoryId != null, Setmeal::getCategoryId, categoryId);
        queryWrapper.eq(status != null, Setmeal::getStatus, status);

        setmealService.page(page, queryWrapper);

        PageResult pageResult = new PageResult();

        pageResult.setTotal(page.getTotal());
        List<SetmealVO> setmealVOList = new ArrayList<>();

        for (Setmeal record : page.getRecords()) {
            SetmealVO setmealVO = new SetmealVO();
            BeanUtils.copyProperties(record,setmealVO);
            setmealVO.setCategoryName(categoryService.getById(record.getCategoryId()).getName());
            setmealVOList.add(setmealVO);
        }
        pageResult.setRecords(setmealVOList);

        return Result.success(pageResult);
    }

    // 新增套餐
    @PostMapping
    public Result save(@RequestBody SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();

        BeanUtils.copyProperties(setmealDTO,setmeal);

        setmeal.setCreateUser(BaseContext.getCurrentId());
        setmeal.setUpdateUser(BaseContext.getCurrentId());
        setmeal.setCreateTime(LocalDateTime.now());
        setmeal.setUpdateTime(LocalDateTime.now());

        setmealService.save(setmeal);

        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();

        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmeal.getId());
            setmealDishService.save(setmealDish);
        }

        return Result.success();
    }

    // 根据 id 获取套餐信息
    @GetMapping("/{id}")
    public Result getById(@PathVariable String id){
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(Setmeal::getId,id);
        Setmeal setmeal = setmealService.getOne(queryWrapper);

        SetmealDTO setmealDTO = new SetmealDTO();
        BeanUtils.copyProperties(setmeal,setmealDTO);

        LambdaQueryWrapper<SetmealDish> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(SetmealDish::getSetmealId,setmealDTO.getId());

        List<SetmealDish> setmealDishList = setmealDishService.list(queryWrapper1);

        setmealDTO.setSetmealDishes(setmealDishList);

        return Result.success(setmealDTO);
    }

    // 根据 ids 批量删除
    @DeleteMapping
    public Result deleteByIds(String ids){
        String[] idsArr = ids.split(",");
        setmealService.removeByIds(Arrays.asList(idsArr));
        return Result.success();
    }

    // 修改
    @PutMapping
    public Result update(@RequestBody SetmealDTO setmealDTO){
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);

        setmealService.updateById(setmeal);

        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.eq(SetmealDish::getSetmealId,setmeal.getId());

        setmealDishService.remove(setmealDishLambdaQueryWrapper);

        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmeal.getId());
            setmealDishService.save(setmealDish);
        }
        return Result.success("更新成功");
    }

    // 修改套餐状态
    @PostMapping("/status/{status}")
    public Result status(@PathVariable int status,Setmeal setmeal){
        setmeal.setStatus(status);
        setmealService.updateById(setmeal);
        return Result.success("修改成功");
    }
}
