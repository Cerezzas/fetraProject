package az.coders.fera_project.repository.register;

import az.coders.fera_project.entity.register.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);



    boolean existsByEmail(String email); // если понадобится

}
