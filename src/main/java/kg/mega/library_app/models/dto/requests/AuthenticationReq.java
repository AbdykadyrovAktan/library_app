package kg.mega.library_app.models.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import static kg.mega.library_app.models.constants.RegExp.PASSWORD;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticationReq {
    @Email(message = "Invalid email address")
    @Schema(example = "abdykadyrovkarakol@gmail.com")
    String email;
    @NotBlank(message = "Password shouldn't be empty")
    @Pattern(regexp = PASSWORD, message = "Invalid password")
    @Schema(example = "Khabib27medina")
    String password;
}
