package com.codermast.sky.controller.admin;

import com.codermast.sky.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("adminShopController")
@RequestMapping("/admin/shop")
public class ShopController {

    private static final String KEY = "SHOP_STATUS";

    @Autowired
    private RedisTemplate redisTemplate;

    // 更改店铺状态
    @PutMapping("/{status}")
    public Result setStatus(@PathVariable Integer status){

        redisTemplate.opsForValue().set(KEY,status);

        return Result.success();
    }

    // 查询店铺状态
    @GetMapping("/status")
    public Result getStatus(){
        Integer status = (Integer) redisTemplate.opsForValue().get(KEY);

        return Result.success(status);
    }
}
