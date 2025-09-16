package az.coders.fera_project.models;

import lombok.Data;

@Data
public class UserRequest {
    private Integer id;
    private String username;
    private String password;
    private String email;
}
