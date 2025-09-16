package az.coders.fera_project.service;

import az.coders.fera_project.dto.register.UserDto;
import az.coders.fera_project.models.UserRequest;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;


public interface UserService extends UserDetailsService {
    List<UserDto> getUsers();

    UserDto getUserById(Integer id);

    UserDto createUser(UserDto userDto);

    void deleteUser(Integer id);

    UserDto updateUser(Integer id, UserDto userDto);
    UserRequest updateUser(Integer id, UserRequest userRequest);

    void selectAddress(Long userId, Integer addressId);

}
