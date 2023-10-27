package dev.arif.userservice.dtos;

import dev.arif.userservice.models.Role;
import dev.arif.userservice.models.User;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
@Setter

public class UserDto {
    private String email;
    private Set<Role> roles = new HashSet<>();

    public static UserDto from(User user) {
        UserDto userDto = new UserDto();
        userDto.setEmail(user.getEmail());
        userDto.setRoles(user.getRoles());

        return userDto;
    }
}
