package ru.practicum.ewm.model.user;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDto {
    long id;

    @NotNull(message = "email не может быть пустым")
    @Email(message = "email введен не верно")
    @NotBlank
    @Size(min = 6, max = 254)
    String email;

    @NotNull(message = "Имя не может быть пустым")
    @NotBlank(message = "Имя не может быть пустым или содержать только пробелы")
    @Size(min = 2, max = 250)
    String name;
}
