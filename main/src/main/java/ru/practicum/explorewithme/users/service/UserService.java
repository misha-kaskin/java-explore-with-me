package ru.practicum.explorewithme.users.service;

import ru.practicum.explorewithme.users.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto addUser(UserDto user);

    List<UserDto> findUsers(List<Long> ids, Integer from, Integer size);

    void deleteUser(Long id);
}
