package com.sky.controller.user;


import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("usershopcontroller")
@Api("店铺接口")
@RequestMapping("/user/shop")
public class ShopController {

    public static final String KEY = "Shop_Status";

    @Autowired
    private RedisTemplate redisTemplate;


    @GetMapping("/status")
    @ApiOperation("获取营业状态")
    public  Result<Integer> getstatus(){
        Integer shopStatus = (Integer) redisTemplate.opsForValue().get(KEY);
        return  Result.success(shopStatus);
    }

}
