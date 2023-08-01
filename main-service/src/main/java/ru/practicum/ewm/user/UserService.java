package ru.practicum.ewm.user;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.model.UserDto;

import java.util.List;

@Transactional(readOnly = true)
public interface UserService {
    List<UserDto> getUsers(List<Long> users, Integer from, Integer size);

    @Transactional(readOnly = true)
    UserDto saveUser(UserDto userDto);

    @Transactional(readOnly = true)
    void deleteUser(Long userId);

    User findById(Long userId);
}
