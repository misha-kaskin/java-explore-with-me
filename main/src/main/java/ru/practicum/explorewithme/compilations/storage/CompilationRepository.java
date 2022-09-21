package ru.practicum.explorewithme.compilations.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.explorewithme.compilations.dto.CompilationDto;

public interface CompilationRepository extends JpaRepository<CompilationDto, Long> {
    @Query("select c from CompilationDto c where c.pinned = ?1")
    Page<CompilationDto> findAllPinnedCompilations(Boolean pinned, Pageable pageable);
}
