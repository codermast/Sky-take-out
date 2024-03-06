package com.codermast.sky.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.codermast.sky.dto.DishDTO;
import com.codermast.sky.dto.DishPageQueryDTO;
import com.codermast.sky.entity.Category;
import com.codermast.sky.entity.Dish;
import com.codermast.sky.entity.DishFlavor;
import com.codermast.sky.result.PageResult;
import com.codermast.sky.result.Result;
import com.codermast.sky.service.CategoryService;
import com.codermast.sky.service.DishFlavorService;
import com.codermast.sky.service.DishService;
import com.codermast.sky.vo.DishVO;
import io.swagger.models.auth.In;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/admin/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    // 分页查询
    @GetMapping("/page")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){
        int pageNum = dishPageQueryDTO.getPage();
        int pageSize = dishPageQueryDTO.getPageSize();
        String name = dishPageQueryDTO.getName();
        Integer status = dishPageQueryDTO.getStatus();
        Integer categoryId = dishPageQueryDTO.getCategoryId();

        Page<Dish> page = new Page<>(pageNum,pageSize);

        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(name != null,Dish::getName,name);
        queryWrapper.eq(status != null,Dish::getStatus,status);
        queryWrapper.eq(categoryId != null,Dish::getCategoryId,categoryId);

        dishService.page(page,queryWrapper);

        PageResult pageResult = new PageResult();

        List<DishVO> dishVOList = new ArrayList<>();
        for (Dish record : page.getRecords()) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(record,dishVO);

            Category category = categoryService.getById(record.getCategoryId());

            dishVO.setCategoryName(category.getName());
            dishVOList.add(dishVO);
        }

        pageResult.setRecords(dishVOList);

        return Result.success(pageResult);
    }

    @GetMapping("/{id}")
    public Result<DishDTO> getById(@PathVariable Long id){
        Dish dish = dishService.getById(id);
        DishDTO dishDTO = new DishDTO();
        BeanUtils.copyProperties(dish,dishDTO);
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,id);

        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        dishDTO.setFlavors(flavors);
        return Result.success(dishDTO);
    }

    // 更新菜品
    @PutMapping
    public Result update(@RequestBody DishDTO dishDTO){

        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishService.updateById(dish);

        List<DishFlavor> flavorList = dishDTO.getFlavors();

        for (DishFlavor dishFlavor : flavorList) {
            dishFlavor.setDishId(dishDTO.getId());
            dishFlavorService.saveOrUpdate(dishFlavor);
        }
        return Result.success();
    }

    // 根据 id 批量删除
    @DeleteMapping
    public Result removeListByIds(String ids){

        String[] idList = ids.split(",");
        List<Long> list = new ArrayList<>();

        for (String s : idList) {
            list.add(Long.parseLong(s));
        }

        dishService.removeByIds(list);

        return Result.success();
    }

    @PostMapping("/status/{status}")
    public Result changeStatus(@PathVariable int status,Dish dish){
        dish.setStatus(status);

        dishService.updateById(dish);

        return Result.success();
    }

    // 根据 categoryId 查 dishList
    @GetMapping("/list")
    public Result<List<Dish>> list(Long categoryId){
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(Dish::getCategoryId,categoryId);

        List<Dish> list = dishService.list(queryWrapper);

        return Result.success(list);
    }
}
