package courses.concordia.dto.model.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import courses.concordia.model.User;
import lombok.*;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserResponseDto {
    private String id;
    private String username;
    private boolean verified;

    public static UserResponseDto fromEntity(User user) {
        return new UserResponseDto()
                .setId(user.get_id())
                .setUsername(user.getUsername())
                .setVerified(user.isVerified());
    }
}
