package az.coders.fera_project;

import az.coders.fera_project.entity.register.Authority;
import az.coders.fera_project.entity.register.User;
import az.coders.fera_project.repository.register.AuthorityRepository;
import az.coders.fera_project.repository.register.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@SpringBootApplication
@RequiredArgsConstructor
public class FeraProjectApplication implements CommandLineRunner {

	private final UserRepository userRepository;
	private final AuthorityRepository authorityRepository;
	private final PasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(FeraProjectApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		// Создание ролей, если их нет
		Authority authorityUser = authorityRepository.findByAuthority("ROLE_USER");
		if (authorityUser == null) {
			authorityUser = new Authority();
			authorityUser.setAuthority("ROLE_USER");
			authorityRepository.save(authorityUser);
		}

		Authority authorityAdmin = authorityRepository.findByAuthority("ROLE_ADMIN");
		if (authorityAdmin == null) {
			authorityAdmin = new Authority();
			authorityAdmin.setAuthority("ROLE_ADMIN");
			authorityRepository.save(authorityAdmin);
		}

		// Создание только администратора
		if (!userRepository.existsByUsername("admin")) {
			User admin = new User();
			admin.setUsername("admin");
			admin.setPassword(passwordEncoder.encode("admin123"));  // пароль можно выбрать любой
			admin.setEmail("admin@gmail.com");
			admin.setAuthorities(List.of(
					authorityUser,
					authorityAdmin
			));
			admin.setEnabled(true);
			userRepository.save(admin);
		}
	}
}


//		Authority authorityUser = new Authority();
//		authorityUser.setAuthority("ROLE_USER");
//		authorityRepository.save(authorityUser);
//		Authority authorityAdmin = new Authority();
//		authorityAdmin.setAuthority("ROLE_ADMIN");
//		authorityRepository.save(authorityAdmin);
//		User user = new User();
//		user.setUsername("user");
//		user.setPassword(passwordEncoder.encode("1234"));
//		user.setAuthorities(List.of(authorityUser));
//		userRepository.save(user);
//		User userAdmin = new User();
//		userAdmin.setUsername("admin");
//		userAdmin.setPassword(passwordEncoder.encode("12345"));
//		userAdmin.setAuthorities(List.of(authorityUser,authorityAdmin));
//		userRepository.save(userAdmin);

