package com.hotpot.service;

import com.hotpot.entity.Dish;
import com.hotpot.entity.DishCategory;
import java.util.List;

public interface DishService {
    List<DishCategory> getCategories();
    List<Dish> getDishes(Long categoryId, String keyword);
    Dish getDishDetail(Long id);
    List<Dish> getTopSales(int limit);
    // Admin
    List<Dish> adminList();
    void saveDish(Dish dish);
    void deleteDish(Long id);
    void saveCategory(DishCategory category);
    void deleteCategory(Long id);
}
