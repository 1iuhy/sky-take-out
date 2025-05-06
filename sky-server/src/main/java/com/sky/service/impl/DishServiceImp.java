package com.sky.service.impl;

import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.mapper.DishMapper;
import com.sky.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service    //业务层的实现类加入@Service注解
@Slf4j
public class DishServiceImp implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Override
    /**
     * 添加菜品和对应口味
     */
    @Transactional
    public void saveWithDish(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        //在菜品表中插入一条数据
        dishMapper.insert(dish);
        //在口味表中插入n条数据
    }
}
