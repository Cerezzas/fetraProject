package az.coders.fera_project.repository.register;

import az.coders.fera_project.entity.register.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, Integer> {
    boolean existsByAuthority(String authority);

    Authority findByAuthority(String authority); // если хочешь Optional<>, можешь тоже обернуть
}
