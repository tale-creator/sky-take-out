package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.HashMap;

@Mapper
public interface UserMapper {
    @Select("select * from sky_take_out.user where openid = #{openid}")
    User getuserbyopenid(String openid);

    @Select("select * from sky_take_out.user where id = #{id}")
    User getById(Long id);

    void insert(User user);

    Integer getByMap(HashMap<String, Object> map);
}
