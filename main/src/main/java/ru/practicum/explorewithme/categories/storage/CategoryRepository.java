package ru.practicum.explorewithme.categories.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explorewithme.categories.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findCategoryById(Long id);
}
