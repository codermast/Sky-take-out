package com.codermast.sky.controller.user;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.codermast.sky.constant.MessageConstant;
import com.codermast.sky.dto.UserLoginDTO;
import com.codermast.sky.entity.User;
import com.codermast.sky.exception.LoginFailedException;
import com.codermast.sky.properties.WeChatProperties;
import com.codermast.sky.result.Result;
import com.codermast.sky.service.UserService;
import com.codermast.sky.utils.HttpClientUtil;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user/user")
public class UserController {

    // 微信服务接口地址
    public static final String WX_LOGIN = "https://api.weixin.qq.com/sns/jscode2session";

    @Autowired
    private UserService userService;

    @Autowired
    private WeChatProperties weChatProperties;

    // 用户登录
    @PostMapping("/login")
    public Result login(@RequestBody UserLoginDTO userLoginDTO){
        // 1. 获取 openid
        String openid = getOpenid(userLoginDTO.getCode());

        // 2. 判断 openid 是否为空，如果为空表示登录失败，抛出业务异常
        if (openid == null){
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getOpenid,openid);

        User user = userService.getOne(queryWrapper);
        // 3. 判断当前用户是否为新用户
        if (user == null){
            // 4. 如果是新用户，自动完成注册
            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            userService.save(user);
        }

        // 5. 返回这个用户对象

        return  Result.success(user);
    }

    // 1. 调用微信接口服务，获得当前微信用户的 openid
    private String getOpenid(String code){
        Map<String, String> map = new HashMap<>();
        map.put("appid",weChatProperties.getAppid());
        map.put("secret",weChatProperties.getSecret());
        map.put("js_code",code);
        map.put("grant_type","authorization_code");

        String retJson = HttpClientUtil.doGet(WX_LOGIN, map);
        JSONObject jsonObject = JSON.parseObject(retJson);
        String openid = jsonObject.getString("openid");
        return openid;
    }
}
