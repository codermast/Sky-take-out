package com.codermast.sky.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.codermast.sky.context.BaseContext;
import com.codermast.sky.dto.CategoryDTO;
import com.codermast.sky.dto.CategoryPageQueryDTO;
import com.codermast.sky.entity.Category;
import com.codermast.sky.result.PageResult;
import com.codermast.sky.result.Result;
import com.codermast.sky.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/category")
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    @GetMapping("/page")
    public Result<PageResult> page(CategoryPageQueryDTO categoryPageQueryDTO) {
        int pageNum = categoryPageQueryDTO.getPage();
        int pageSize = categoryPageQueryDTO.getPageSize();
        String name = categoryPageQueryDTO.getName();
        Integer type = categoryPageQueryDTO.getType();

        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Category::getId);
        queryWrapper.like(name != null, Category::getName, name);
        queryWrapper.eq(type != null, Category::getType, type);
        Page<Category> page = new Page<>(pageNum, pageSize);

        categoryService.page(page, queryWrapper);

        PageResult pageResult = new PageResult();

        pageResult.setRecords(page.getRecords());
        pageResult.setTotal(page.getTotal());

        return Result.success(pageResult);
    }

    // 新增分类
    @PostMapping
    public Result save(@RequestBody CategoryDTO categoryDTO){
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO,category);

        category.setCreateTime(LocalDateTime.now());
        category.setCreateUser(BaseContext.getCurrentId());
        category.setUpdateTime(LocalDateTime.now());
        category.setUpdateUser(BaseContext.getCurrentId());
        category.setStatus(1);

        categoryService.save(category);

        return Result.success();
    }

    // 更新分类
    @PutMapping
    public Result update(@RequestBody CategoryDTO categoryDTO){
        Category category = new Category();

        BeanUtils.copyProperties(categoryDTO,category);

        category.setUpdateTime(LocalDateTime.now());
        category.setUpdateUser(BaseContext.getCurrentId());

        categoryService.updateById(category);
        return Result.success();
    }

    // 修改分类状态 0：禁用，1：激活
    @PostMapping("/status/{status}")
    public Result status(@PathVariable int status,Category category){
        category.setStatus(status);

        category.setUpdateTime(LocalDateTime.now());
        category.setUpdateUser(BaseContext.getCurrentId());
        categoryService.updateById(category);
        return Result.success();
    }

    // 删除分类
    @DeleteMapping
    public Result delete(Long id){
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(Category::getId,id);
        boolean remove = categoryService.remove(queryWrapper);

        if (remove){
            return Result.success();
        }else {
            return Result.error("删除失败");
        }
    }

    // 获取分类列表
    @GetMapping("/list")
    public Result list(Long type){
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getType,type);

        List<Category> list = categoryService.list(queryWrapper);
        return Result.success(list);
    }
}
