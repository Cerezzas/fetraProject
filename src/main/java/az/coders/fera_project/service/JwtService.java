package az.coders.fera_project.service;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.Authentication;

public interface JwtService {
    Claims parseToken(String token);
    String issueToken(Authentication authentication);
}
