package az.coders.fera_project.dto.register;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

@Data
public class AuthorityDto implements GrantedAuthority {
    private Integer id;
    private String authority;
}