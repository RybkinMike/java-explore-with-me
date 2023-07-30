package ru.practicum.ewm.admin;

import ru.practicum.ewm.model.category.CategoryDto;
import ru.practicum.ewm.model.compilation.CompilationDto;
import ru.practicum.ewm.model.compilation.NewCompilationDto;
import ru.practicum.ewm.model.compilation.UpdateCompilationRequest;
import ru.practicum.ewm.model.event.EventFullDto;
import ru.practicum.ewm.model.event.UpdateEventAdminRequest;
import ru.practicum.ewm.model.user.UserDto;

import java.util.List;

public interface AdminService {
    CategoryDto saveCategory(CategoryDto categoryDto);

    void deleteCategory(Long catId);

    CategoryDto patchCategory(CategoryDto categoryDto, Long catId);

    List<EventFullDto> getEvents(List<Long> users, List<String> states, List<Long> categories, String rangeStart, String rangeEnd, Integer from, Integer size);

    EventFullDto patchEvent(UpdateEventAdminRequest updateEventAdminRequest, Long eventId);

    List<UserDto> getUsers(List<Long> users, Integer from, Integer size);

    UserDto saveUser(UserDto userdto);

    void deleteUser(Long userId);

    CompilationDto saveCompilation(NewCompilationDto newCompilationDto);

    void deleteCompilation(Long compId);

    CompilationDto patchCompilation(UpdateCompilationRequest updateCompilationRequest, Long compId);
}
