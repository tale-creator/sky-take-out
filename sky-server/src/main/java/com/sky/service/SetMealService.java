package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetMealService {
    void save(SetmealDTO setmealDTO);

    SetmealVO getsetmealbyid(Long id);

    void update(SetmealDTO setmealDTO);

    void deleteBatch(List<Long> ids);

    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    void startOrStop(Integer status, Long id);

    List<Setmeal> list(Setmeal setmeal);

    List<DishItemVO> getDishItemById(Long id);
}
