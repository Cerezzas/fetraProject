package az.coders.fera_project.entity;

import az.coders.fera_project.enums.AddressType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;


import az.coders.fera_project.entity.register.User;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Address {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore // предотвращает цикл Address → User → Address
    private User user;  // Связь с пользователем

    private String name;             // "Steve Smith"
    private String phoneNumber;      // "(907) 555-0101"
    private String street;           // "Ranchview Dr, Richardson, California"
    private String city;             // "California"
    private String country;          // "United States"

    @Enumerated(EnumType.STRING)
    private AddressType addressType; // home/work

    private boolean isDefault;       // "Make this my default address"


}
