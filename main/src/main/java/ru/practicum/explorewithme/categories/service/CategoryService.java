package ru.practicum.explorewithme.categories.service;

import ru.practicum.explorewithme.categories.dto.Category;

import java.util.List;

public interface CategoryService {
    Category addCategory(Category categoryDto);

    Category updateCategory(Category categoryDto);

    List<Category> getCategories(Integer from, Integer size);

    Category getCategoryById(Long id);

    void deleteCategoryById(Long id);
}
