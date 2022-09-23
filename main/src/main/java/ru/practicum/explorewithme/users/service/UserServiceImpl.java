package ru.practicum.explorewithme.users.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.exception.ValidationException;
import ru.practicum.explorewithme.users.dto.QUser;
import ru.practicum.explorewithme.users.dto.User;
import ru.practicum.explorewithme.users.storage.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public User addUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ValidationException("Добавление существующего email");
        }

        return userRepository.save(user);
    }

    @Override
    public List<User> findUsers(List<Long> ids, Integer from, Integer size) {
        if (ids == null) {
            return userRepository.findAll(PageRequest.of(from, size)).toList();
        }

        BooleanExpression booleanExpression = QUser.user.id.in(ids);

        List<User> userDtoList = userRepository
                .findAll(booleanExpression, PageRequest.of(from, size)).stream()
                .collect(Collectors.toList());

        if (userDtoList.isEmpty()) {
            throw new NotFoundException("Пользователи не найдены");
        }

        return userDtoList;
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("Удаление несуществующего пользователя");
        }

        userRepository.deleteById(id);
    }
}
