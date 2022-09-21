package ru.practicum.explorewithme.categories.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.explorewithme.categories.dto.CategoryDto;

public interface CategoryRepository extends JpaRepository<CategoryDto, Long> {
    @Query("select c from CategoryDto c where c.id = ?1")
    CategoryDto findCategoryDtoById(Long id);
}
