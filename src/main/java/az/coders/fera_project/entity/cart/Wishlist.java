package az.coders.fera_project.entity.cart;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import az.coders.fera_project.entity.register.User;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Wishlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
     private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)  // связь с пользователем
    @JsonIgnore // ✅ Добавь это
    private User user;  // связь с пользователем

    @Column(name = "session_key")
    private String sessionKey; // уникальный идентификатор гостя

    @OneToMany(mappedBy = "wishlist", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WishlistItem> items = new ArrayList<>();  // список товаров в вишлисте

}
