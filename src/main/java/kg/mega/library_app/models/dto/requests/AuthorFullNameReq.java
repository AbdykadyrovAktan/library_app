package kg.mega.library_app.models.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import static kg.mega.library_app.models.constants.RegExp.NAME;
import static lombok.AccessLevel.PRIVATE;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = PRIVATE)
public class AuthorFullNameReq {
    @JsonProperty("firstname")
//    @NotBlank(message = "Invalid author firstname: EMPTY")
//    @NotNull(message = "Invalid author firstname: NULL")
    @Pattern(regexp = NAME, message = "Invalid author firstname")
    @Schema(example = "Chuck")
    String authorFirstname;
    @JsonProperty("lastname")
//    @NotBlank(message = "Invalid author lastname: EMPTY")
//    @NotNull(message = "Invalid author lastname: NULL")
    @Pattern(regexp = NAME, message = "Invalid author lastname")
    @Schema(example = "Palahniuk")
    String authorLastname;
}
