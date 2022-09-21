package ru.practicum.explorewithme.users.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.explorewithme.users.dto.UserDto;

import java.util.List;

public interface UserRepository extends JpaRepository<UserDto, Long>, QuerydslPredicateExecutor<UserDto> {
    @Query("select u from UserDto u where u.id in ?1")
    Page<UserDto> findAllById(List<Long> ids, Pageable pageable);
}
