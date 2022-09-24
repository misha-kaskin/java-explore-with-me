package ru.practicum.explorewithme.users.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.explorewithme.users.model.User;

public interface UserRepository extends JpaRepository<User, Long>, QuerydslPredicateExecutor<User> {
    @Query("select case when (count(u) > 0) then true else false end " +
            "from User u " +
            "where u.email = ?1")
    Boolean existsByEmail(String email);
}
