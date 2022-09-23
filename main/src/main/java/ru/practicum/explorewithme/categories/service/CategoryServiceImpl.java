package ru.practicum.explorewithme.categories.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.categories.dto.Category;
import ru.practicum.explorewithme.categories.storage.CategoryRepository;
import ru.practicum.explorewithme.events.storage.EventRepository;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.exception.ValidationException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public Category addCategory(Category categoryDto) {
        return categoryRepository.save(categoryDto);
    }

    @Override
    @Transactional
    public Category updateCategory(Category categoryDto) {
        if (categoryDto.getId() == null) {
            throw new ValidationException("Пустой идентификатор");
        }
        return categoryRepository.save(categoryDto);
    }

    @Override
    public List<Category> getCategories(Integer from, Integer size) {
        return categoryRepository.findAll(PageRequest.of(from, size)).toList();
    }

    @Override
    public Category getCategoryById(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new NotFoundException("Категория не найдена");
        }
        return categoryRepository.findCategoryById(id);
    }

    @Override
    @Transactional
    public void deleteCategoryById(Long id) {
        if (eventRepository.existsByCategoryId(id)) {
            throw new NotFoundException("Удаление категории с привязанными событиями");
        }
        categoryRepository.deleteById(id);
    }
}
