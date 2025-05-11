package com.sky.service;

import com.sky.dto.SetmealDTO;
import org.springframework.stereotype.Service;


public interface SetmealService {
    /**
     * 新增套餐并且保存菜品和套餐的关系
     * @param setmealDTO
     */
    void saveWithDish(SetmealDTO setmealDTO);
}
