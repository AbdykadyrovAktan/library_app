package kg.mega.library_app.models.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import static kg.mega.library_app.models.constants.RegExp.*;
import static lombok.AccessLevel.PRIVATE;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
public class RegistrationReq {
    @NotBlank(message = "Invalid firstname: EMPTY")
    @NotNull(message = "Invalid firstname: NULL")
    @Pattern(regexp = NAME, message = "Invalid firstname")
    @Schema(example = "Aktan")
    String firstname;
    @NotBlank(message = "Invalid lastname: EMPTY")
    @NotNull(message = "Invalid lastname: NULL")
    @Pattern(regexp = NAME, message = "Invalid lastname")
    @Schema(example = "Abdykadyrov")
    String lastname;
    @JsonProperty("phone_number")
    @NotBlank(message = "Invalid phone number: EMPTY")
    @NotNull(message = "Invalid phone number: NULL")
    @Pattern(regexp = PHONE_NUMBER, message = "Invalid phone number")
    @Schema(example = "+996550785352")
    String phoneNumber;
    @Email(message = "Invalid email address")
    @Schema(example = "abdykadyrovkarakol@gmail.com")
    String email;
    @NotBlank(message = "Password shouldn't be empty")
    @Pattern(regexp = PASSWORD, message = "Invalid password")
    @Schema(example = "Khabib27medina")
    String password;
}