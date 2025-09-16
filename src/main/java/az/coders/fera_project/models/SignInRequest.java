package az.coders.fera_project.models;

import lombok.Data;

@Data
public class SignInRequest {
    private String username;
    private String password;
}
