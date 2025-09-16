package az.coders.fera_project.service.impl.security;

import az.coders.fera_project.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

@Service
public class JwtServiceImpl implements JwtService {
   @Value("${spring.security.jwt.key}")
   private String key;
    @Value("${spring.security.jwt.access-expire-time}")
    private long expireTime;
    @Override
    public Claims parseToken(String token) {
        return (Claims) Jwts.parser().verifyWith((SecretKey) getSignKey() ).build().parse(token).getPayload();
    }

    @Override
    public String issueToken(Authentication authentication) {
        return Jwts.builder().header()
                .add("typ", "JWT")
                .add("alg", "HS256")
                .and().claims()
                .subject(authentication.getName())
                .add("principal", authentication.getPrincipal())
                .add("authorities", authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .issuedAt(new Date())
                .expiration(new Date (new Date().getTime() + expireTime))
                .and()
                .signWith(getSignKey()).compact();
    }
    public Key getSignKey() {
        byte[] keyBytes =key.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
