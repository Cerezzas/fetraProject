package az.coders.fera_project.dto.register;

import lombok.Data;

import java.util.List;

@Data
public class UserDto {
    private Integer id;
    private String username;
    private String password;
    private boolean accountNonExpired = true;
    private boolean accountNonLocked = true;
    private boolean credentialsNonExpired = true;
    private boolean enabled = true;
    List<AuthorityDto> authorities;
}