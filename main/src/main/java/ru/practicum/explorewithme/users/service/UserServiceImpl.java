package ru.practicum.explorewithme.users.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.users.dto.QUserDto;
import ru.practicum.explorewithme.users.dto.UserDto;
import ru.practicum.explorewithme.users.storage.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto addUser(UserDto user) {
        return userRepository.save(user);
    }

    @Override
    public List<UserDto> findUsers(List<Long> ids, Integer from, Integer size) {
        if (ids == null) {
            return userRepository.findAll(PageRequest.of(from, size)).toList();
        }

        BooleanExpression booleanExpression = QUserDto.userDto.id.in(ids);

        Iterable<UserDto> userDtoIterable = userRepository
                .findAll(booleanExpression, PageRequest.of(from, size));

        List<UserDto> userDtoList = new ArrayList<>();
        userDtoIterable.forEach(userDtoList::add);

        if (userDtoList.isEmpty()) {
            throw new NotFoundException("Пользователи не найдены");
        }
        return userDtoList;
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("Удаление несуществующего пользователя");
        }
        userRepository.deleteById(id);
    }
}
