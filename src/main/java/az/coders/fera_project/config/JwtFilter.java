package az.coders.fera_project.config;

import az.coders.fera_project.service.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Configuration
public class JwtFilter extends OncePerRequestFilter {
    @Autowired
    private JwtService jwtService;
    public static final String ACCESS_TOKEN = "access-token";
    public static final String REFRESH_TOKEN = "refresh-token";
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        Optional<String> token=Optional.ofNullable(request.getHeader(HttpHeaders.AUTHORIZATION));
//        if(token.isPresent())
        Cookie[] cookies = request.getCookies();
        if(cookies != null) {
            Arrays.stream(cookies).filter(cookie -> cookie.getName().equals(ACCESS_TOKEN)).findFirst().ifPresent(cookie ->
            {
                Claims claims = jwtService.parseToken(cookie.getValue());
                System.out.println("claims" + claims);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(claims.get("principal"), null, getAuthorities(claims));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            });
        }
        filterChain.doFilter(request, response);
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Claims claims) {
        List<String> roles=claims.get("authorities", List.class);
        return roles.stream().map(SimpleGrantedAuthority::new).toList();
    }
}
