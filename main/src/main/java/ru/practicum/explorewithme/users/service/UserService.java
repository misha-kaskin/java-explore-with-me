package ru.practicum.explorewithme.users.service;

import ru.practicum.explorewithme.users.model.User;

import java.util.List;

public interface UserService {
    User addUser(User user);

    List<User> findUsers(List<Long> ids, Integer from, Integer size);

    void deleteUser(Long id);
}
