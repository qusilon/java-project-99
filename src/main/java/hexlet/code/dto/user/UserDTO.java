package hexlet.code.dto.user;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class UserDTO {

    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private LocalDateTime createdAt;

}
