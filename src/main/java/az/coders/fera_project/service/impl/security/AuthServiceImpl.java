package az.coders.fera_project.service.impl.security;

import az.coders.fera_project.config.JwtFilter;
import az.coders.fera_project.dto.register.RefreshTokenDto;
import az.coders.fera_project.dto.register.RegisterRequest;
import az.coders.fera_project.entity.register.PasswordResetToken;
import az.coders.fera_project.entity.register.RefreshToken;
import az.coders.fera_project.entity.register.User;
import az.coders.fera_project.exception.NotFoundException;
import az.coders.fera_project.models.AccessTokenResponse;
import az.coders.fera_project.models.SignInRequest;
import az.coders.fera_project.models.SignInResponse;
import az.coders.fera_project.repository.register.AuthorityRepository;
import az.coders.fera_project.repository.register.PasswordResetTokenRepository;
import az.coders.fera_project.repository.register.RefreshTokenRepository;
import az.coders.fera_project.repository.register.UserRepository;
import az.coders.fera_project.service.AuthService;
import az.coders.fera_project.service.JwtService;
import az.coders.fera_project.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    @Value("${spring.security.jwt.access-expire-time}")
    private Integer accessExpireTime;
    @Value("${spring.security.jwt.refresh-expire-time}")
    private Integer refreshExpireTime;
    private final ObjectMapper objectMapper;
    private final RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtService jwtService;
    private final UserService userService;

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;


    @Override
    public SignInResponse signIn(SignInRequest signInRequest) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(signInRequest.getUsername(), signInRequest.getPassword());
        Authentication authenticate = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        String token = jwtService.issueToken(authenticate);
        AccessTokenResponse accessTokenResponse =new AccessTokenResponse(token);
        RefreshTokenDto refreshTokenDTO=issueRefreshToken(signInRequest.getUsername());
        return new SignInResponse(accessTokenResponse,refreshTokenDTO);
    }

    private RefreshTokenDto issueRefreshToken(String username) {
        RefreshToken refreshToken=new RefreshToken(UUID.randomUUID().toString(),username,true,new Date(),new Date(System.currentTimeMillis()+refreshExpireTime));
        refreshTokenRepository.save(refreshToken);
        return objectMapper.convertValue(refreshToken,RefreshTokenDto.class);
    }
    @Override
    public void setCookies(HttpHeaders httpHeaders, SignInResponse signInResponse){
        ResponseCookie accessCookie=ResponseCookie.from(JwtFilter.ACCESS_TOKEN,signInResponse.getAccessToken().getToken())
                .maxAge(accessExpireTime)
                .path("/")
                .secure(false)
                .httpOnly(true)
                .sameSite("LAX") //LAX, STRICT , NONE
                .build();
        httpHeaders.add(HttpHeaders.SET_COOKIE, accessCookie.toString());
        ResponseCookie refreshCookie=ResponseCookie.from(JwtFilter.REFRESH_TOKEN,signInResponse.getRefreshToken().getToken())
                .maxAge(accessExpireTime)
                .path("/")
                .secure(false)
                .httpOnly(true)
                .sameSite("LAX") //LAX, STRICT , NONE
                .build();
        httpHeaders.add(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }
    @Override
    public  void clearCookie(HttpHeaders headers) {
        ResponseCookie accessCookie=ResponseCookie.from(JwtFilter.ACCESS_TOKEN,"")
                .maxAge(0)
                .path("/")
                .secure(false)
                .httpOnly(true)
                .sameSite("LAX") //LAX, STRICT , NONE
                .build();
        headers.add(HttpHeaders.SET_COOKIE, accessCookie.toString());
        ResponseCookie refreshCookie=ResponseCookie.from(JwtFilter.REFRESH_TOKEN,"")
                .maxAge(0)
                .path("/")
                .secure(false)
                .httpOnly(true)
                .sameSite("LAX") //LAX, STRICT , NONE
                .build();
        headers.add(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }

    @Override
    public void signOut(String refreshToken) {
        refreshTokenRepository.findByToken(refreshToken).
                ifPresent(refresh -> {
                    refresh.setValid(false);
                    refreshTokenRepository.save(refresh);
                });
    }

    @Override
    public SignInResponse refreshCookie(String refreshToken) {
       

        RefreshToken refreshTokenDb = refreshTokenRepository.findByToken(refreshToken).orElseThrow(
                () -> new BadCredentialsException("Refresh not found"));
        if (!refreshTokenDb.getValid())
            throw new BadCredentialsException("Invalid refresh token");
        if (refreshTokenDb.getExpiresDate().before(new Date()))
            throw new BadCredentialsException("Expired refresh token");
        UserDetails userDetails = userService.loadUserByUsername(refreshTokenDb.getUserName());
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), userDetails.getPassword());
        AccessTokenResponse accessTokenResponse = new AccessTokenResponse(jwtService.issueToken(usernamePasswordAuthenticationToken));
        refreshTokenDb.setValid(false);
        refreshTokenRepository.save(refreshTokenDb);
        RefreshTokenDto refreshTokenDto = issueRefreshToken(refreshTokenDb.getUserName());
        return new SignInResponse(accessTokenResponse, refreshTokenDto);
    }


    @Override
    public void registerUser(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(true);
        user.setAuthorities(List.of(authorityRepository.findByAuthority("ROLE_USER")));
        userRepository.save(user);
    }

    @Transactional
    @Override
    public void sendResetPasswordLink(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User with this email not found"));

        // Удаляем предыдущий токен, если он есть
        passwordResetTokenRepository.deleteByUser(user);

        // Создаем новый токен
        String token = UUID.randomUUID().toString();
        LocalDateTime expiry = LocalDateTime.now().plusHours(1);
        PasswordResetToken resetToken = new PasswordResetToken(token, user, expiry);
        passwordResetTokenRepository.save(resetToken);

        String resetLink = "http://localhost:8080/auth/reset-password?token=" + token;
        System.out.println("Reset password link: " + resetLink);
    }


    @Override
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new NotFoundException("Invalid reset token"));

        if (resetToken.getExpirationDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token has expired");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        passwordResetTokenRepository.delete(resetToken);
    }



}

