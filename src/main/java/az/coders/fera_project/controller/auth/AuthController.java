package az.coders.fera_project.controller.auth;

import az.coders.fera_project.config.JwtFilter;
import az.coders.fera_project.dto.register.ForgotPasswordRequest;
import az.coders.fera_project.dto.register.RegisterRequest;
import az.coders.fera_project.dto.register.ResetPasswordRequest;
import az.coders.fera_project.models.SignInRequest;
import az.coders.fera_project.models.SignInResponse;
import az.coders.fera_project.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    @PostMapping("/sign-in")
    public ResponseEntity< SignInResponse> token(@RequestBody SignInRequest signInRequest){
        SignInResponse signInResponse = authService.signIn(signInRequest);
        authService.signIn( signInRequest );
        HttpHeaders headers = new HttpHeaders();
        authService.setCookies(headers, signInResponse);
        return new ResponseEntity<>(signInResponse,headers, HttpStatus.OK);
    }


    @PostMapping("/sign-out")
    public ResponseEntity<?> signout(@CookieValue(name= JwtFilter.REFRESH_TOKEN) String refreshToken){
        authService.signOut(refreshToken);
        HttpHeaders headers = new HttpHeaders();
        authService.clearCookie(headers);
        return new ResponseEntity<>(headers, HttpStatus.NO_CONTENT);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@CookieValue(name = "refresh-token") String refreshToken) {
        SignInResponse signInResponse = authService.refreshCookie(refreshToken);
        HttpHeaders headers = new HttpHeaders();
        authService.setCookies(headers, signInResponse);
        return new ResponseEntity<>(headers, HttpStatus.NO_CONTENT);

    }

    // ✅ Новый эндпоинт для регистрации
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        authService.registerUser(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        authService.sendResetPasswordLink(request.getEmail());
        return ResponseEntity.ok("Reset link sent to your email.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok("Password successfully reset.");
    }


}