package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetMealDishMapper {
    List<Long> getSetMealDishIdsByMealIds(List<Long> mealId);

    void save(List<SetmealDish> setmealDishList);

    @Select("select * from setmeal_dish where setmeal_id = #{setmealid}")
    List<SetmealDish> getSetMealDishListByid(Long setmealid);

    @Delete("delete from setmeal_dish where setmeal_id = #{setmealId}")
    void deleteBySetmealId(Long setmealId);
}
