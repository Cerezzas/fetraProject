package az.coders.fera_project.entity.register;

import az.coders.fera_project.entity.Address;
import az.coders.fera_project.entity.cart.Cart;
import az.coders.fera_project.entity.cart.Wishlist;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
@Entity
@Data
@Table(name = "users")
public class User implements UserDetails{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(unique = true, nullable = false)
    private String username;
    @Column(nullable = false)
    private String password;
    private boolean accountNonExpired=true;
    private boolean accountNonLocked=true;
    private boolean credentialsNonExpired=true;
    private boolean enabled=true;
    @ManyToMany(fetch = FetchType.EAGER)
    List<Authority> authorities;


    @Column(nullable = false)
    private String email;
//    private String name;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Address> addresses; // Связь с адресами пользователя


    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private Cart cart;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private Wishlist wishlist;  // у пользователя только один вишлист

    // Make this my default address in add Address
    @ManyToOne
    @JoinColumn(name = "selected_address_id")
    private Address selectedAddress;


}
