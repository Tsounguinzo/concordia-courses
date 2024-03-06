package courses.concordia.dto.model.user;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {

    @NotNull
    @NotEmpty
    private String username;

    @NotNull
    @NotEmpty
    private String email;

    @NotNull
    @NotEmpty
    private String password;

    @NotNull
    @NotEmpty
    private Boolean verified;

    public UserDto(String username, String email, String password, Boolean verified) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.verified = verified;
    }

    public UserDto(){}

}
