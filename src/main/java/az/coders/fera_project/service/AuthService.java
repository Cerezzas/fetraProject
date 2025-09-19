package az.coders.fera_project.service;

import az.coders.fera_project.dto.register.RegisterRequest;
import az.coders.fera_project.models.SignInRequest;
import az.coders.fera_project.models.SignInResponse;
import org.springframework.http.HttpHeaders;

public interface AuthService {
    SignInResponse signIn(SignInRequest signInRequest, String sessionKey);

    void setCookies(HttpHeaders headers, SignInResponse signInResponse);

    void clearCookie(HttpHeaders headers);

//    void signOut(String refreshToken);


    void signOut(String refreshToken, HttpHeaders headers);
    SignInResponse refreshCookie(String refreshToken);

    void registerUser(RegisterRequest request);
    void sendResetPasswordLink(String email);
    void resetPassword(String token, String newPassword);

}
