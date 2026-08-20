package com.sky.controller.admin;


import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("adminshopcontroller")
@Api("店铺接口")
@RequestMapping("/admin/shop")
public class ShopController {

    public static final String KEY = "Shop_Status";

    @Autowired
    private RedisTemplate redisTemplate;
    @PutMapping("/{status}")
    @ApiOperation("设置营业状态")
    public Result setStaus(@PathVariable Integer status){
        redisTemplate.opsForValue().set(KEY,status);
        return  Result.success();
    }

    @GetMapping("/status")
    @ApiOperation("获取营业状态")
    public  Result<Integer> getstatus(){
        Integer shopStatus = (Integer) redisTemplate.opsForValue().get(KEY);
        return  Result.success(shopStatus);
    }

}
