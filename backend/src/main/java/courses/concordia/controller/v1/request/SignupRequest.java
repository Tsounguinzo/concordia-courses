package courses.concordia.controller.v1.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SignupRequest {
        @NotNull
        @NotEmpty(message = "Username cannot be empty")
        private String username;
        @NotNull
        @NotEmpty(message = "Email cannot be empty")
        @Email(message = "Email should be valid")
        private String email;
        @NotNull
        @NotEmpty(message = "Password cannot be empty")
        private String password;
}
