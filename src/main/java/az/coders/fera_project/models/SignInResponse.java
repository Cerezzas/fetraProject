package az.coders.fera_project.models;

import az.coders.fera_project.dto.register.RefreshTokenDto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SignInResponse {
    private AccessTokenResponse accessToken;
    private RefreshTokenDto refreshToken;
}
