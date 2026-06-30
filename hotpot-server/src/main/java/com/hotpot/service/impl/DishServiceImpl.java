package com.hotpot.service.impl;

import com.hotpot.common.BusinessException;
import com.hotpot.entity.Dish;
import com.hotpot.entity.DishCategory;
import com.hotpot.mapper.DishMapper;
import com.hotpot.mapper.DishCategoryMapper;
import com.hotpot.service.DishService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DishServiceImpl implements DishService {
    private final DishMapper dishMapper;
    private final DishCategoryMapper categoryMapper;

    @Override
    public List<DishCategory> getCategories() { return categoryMapper.findAll(); }
    @Override
    public List<Dish> getDishes(Long categoryId, String keyword) {
        if (keyword != null && !keyword.isEmpty()) return dishMapper.search(keyword);
        if (categoryId != null) return dishMapper.findByCategory(categoryId);
        return dishMapper.findAll();
    }
    @Override
    public Dish getDishDetail(Long id) {
        Dish d = dishMapper.findById(id);
        if (d == null) throw new BusinessException("菜品不存在");
        return d;
    }
    @Override
    public List<Dish> getTopSales(int limit) { return dishMapper.findTopSales(limit); }
    @Override
    public List<Dish> adminList() { return dishMapper.findAll(); }
    @Override
    public void saveDish(Dish dish) {
        if (dish.getId() == null) dishMapper.insert(dish);
        else dishMapper.update(dish);
    }
    @Override
    public void deleteDish(Long id) { dishMapper.delete(id); }
    @Override
    public void saveCategory(DishCategory category) {
        if (category.getId() == null) categoryMapper.insert(category);
        else categoryMapper.update(category);
    }
    @Override
    public void deleteCategory(Long id) { categoryMapper.delete(id); }
}
