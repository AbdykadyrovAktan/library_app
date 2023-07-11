package kg.mega.library_app.models.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

import static kg.mega.library_app.models.constants.RegExp.NAME;
import static kg.mega.library_app.models.constants.RegExp.PLACE;
import static lombok.AccessLevel.PRIVATE;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = PRIVATE)
public class AuthorReq {
    @JsonProperty("author_firstname")
    @NotBlank(message = "Invalid author firstname: EMPTY")
    @NotNull(message = "Invalid author firstname: NULL")
    @Pattern(regexp = NAME, message = "Invalid author firstname")
    @Schema(example = "Ernest")
    String authorFirstname;
    @JsonProperty("author_lastname")
    @NotBlank(message = "Invalid author lastname: EMPTY")
    @NotNull(message = "Invalid author lastname: NULL")
    @Pattern(regexp = NAME, message = "Invalid author lastname")
    @Schema(example = "Hemingway")
    String authorLastname;
    @JsonProperty("author_date_of_birth")
    @Past(message = "Invalid date of birth")
    @Schema(example = "1899-07-21")
    LocalDate authorDateOfBirth;
    @JsonProperty("author_birthplace")
    @Pattern(regexp = PLACE, message = "Invalid author birthplace")
    @Schema(example = "USA")
    String authorBirthplace;

    public AuthorReq(@NotNull(message = "Invalid author firstname: NULL") String authorFirstname, @NotNull(message = "Invalid author lastname: NULL") String authorLastname) {
        this.authorFirstname = authorFirstname;
        this.authorLastname = authorLastname;
    }
}
