package kg.mega.library_app.models.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import kg.mega.library_app.models.constants.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
public class UserResp {
    String firstname;
    String lastname;
    @JsonProperty("phone_number")
    String phoneNumber;
    @JsonProperty("is_active")
    boolean isActive;
    String email;
    Role role;
}
