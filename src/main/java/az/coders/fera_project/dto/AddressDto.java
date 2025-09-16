package az.coders.fera_project.dto;

import az.coders.fera_project.enums.AddressType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto {
//    private Integer id;
//    private Integer userId;
    private String name;
    private String phoneNumber;
    private String street;
    private String city;
    private String country;
    private AddressType addressType;  // Можно как String или Enum
    private boolean isDefault;
}
