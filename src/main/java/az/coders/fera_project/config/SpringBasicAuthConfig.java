package az.coders.fera_project.config;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SpringBasicAuthConfig {
    @Autowired
    JwtFilter jwtFilter;
@Bean
BCryptPasswordEncoder bCryptPasswordEncoder() {
    return new BCryptPasswordEncoder();
}
//    @Bean
//    public InMemoryUserDetailsManager userDetailsManager() {
//        List<UserDetails> users = new ArrayList<>();
//        List<GrantedAuthority> authorities = new ArrayList<>();
//        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
//        users.add(new User("user", "password", authorities));
//        return new InMemoryUserDetailsManager(new User("guest", "{noop}123", Collections.EMPTY_LIST),
//                new User("user", "{noop}1234", authorities),
//                new User("admin", "{noop}12345", List.of(new SimpleGrantedAuthority("ROLE_USER"),new SimpleGrantedAuthority("ROLE_ADMIN"))));
//    }

//

    //for POSTMAN
//    @Bean
//    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http.
//                csrf(AbstractHttpConfigurer::disable)
//                . authorizeHttpRequests(auth->
//                        auth
//                                //                       .requestMatchers("/public/", "/auth/", "/swagger-ui/","/v3/api-docs/", "/swagger-ui.html").permitAll()
//
////                .requestMatchers("/user/").hasAnyRole("USER", "ADMIN")
////                .requestMatchers("/admin/").hasAuthority("ROLE_ADMIN")
//                                .anyRequest().permitAll())
//                .formLogin(AbstractHttpConfigurer::disable)
//                //.formLogin(Customizer.withDefaults())
//                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
//        // http.apply(jwtFilterConfigurerAdapter);
//
//
//        return http.build();
//    }
//
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // Публичные эндпоинты
                        .requestMatchers("/public/**", "/auth/**", "/categories/**", "/products/**",
                                "/cart/**", "/wishlist/**", "/home/**", "/search",
                                "/medias/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        // Приватные эндпоинты (только авторизованные)
                        .requestMatchers("/checkout/**", "/review/**", "/address/**", "/users/**",
                                "/orders/**", "/payment/**").authenticated()
                        // Роли
                        .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
                        .anyRequest().permitAll()
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setContentType("application/json");
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.getWriter().write("{\"error\":\"You must be logged in to access this resource\"}");
                        })
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
//    @Bean
//    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http.
//        csrf(AbstractHttpConfigurer::disable)
//                . authorizeHttpRequests(auth->auth.requestMatchers("/public/**", "/auth/**","/categories/**","/products/**","/cart/**","/cart/me/apply-discount", "/quick-cart",
//                                "/wishlist/**",
//                                "/home/**", "/search","/medias/**","/swagger-ui/**","/v3/api-docs/**", "/swagger-ui.html").permitAll()
//                        .requestMatchers("/checkout/**","/review/**", "/address/**","/users/**","/orders/**","/payment/**").authenticated()
//                .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN")
//                .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
//                .anyRequest().permitAll())
//                .formLogin(AbstractHttpConfigurer::disable)
//                //.formLogin(Customizer.withDefaults())
//                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
//       // http.apply(jwtFilterConfigurerAdapter);
//
//
//    return http.build();
//    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
return authenticationConfiguration.getAuthenticationManager();

  }

}
