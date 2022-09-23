package ru.practicum.explorewithme.categories.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.categories.dto.Category;
import ru.practicum.explorewithme.categories.service.CategoryService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping("/admin/categories")
    public Category addCategory(@RequestBody @Valid Category categoryDto) {
        return categoryService.addCategory(categoryDto);
    }

    @PatchMapping("/admin/categories")
    public Category updateCategory(@RequestBody @Valid Category categoryDto) {
        return categoryService.updateCategory(categoryDto);
    }

    @GetMapping("/categories")
    public List<Category> getCategories(@RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                        @RequestParam(defaultValue = "10") @Positive Integer size) {
        return categoryService.getCategories(from, size);
    }

    @GetMapping("/categories/{id}")
    public Category getCategoryById(@PathVariable Long id) {
        return categoryService.getCategoryById(id);
    }

    @DeleteMapping("/admin/categories/{id}")
    public void deleteCategoryById(@PathVariable Long id) {
        categoryService.deleteCategoryById(id);
    }
}
