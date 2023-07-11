package kg.mega.library_app.models.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import static kg.mega.library_app.models.constants.RegExp.DESCRIPTION;
import static kg.mega.library_app.models.constants.RegExp.TITLE;
import static lombok.AccessLevel.PRIVATE;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = PRIVATE)
public class GenreReq {
    @NotBlank(message = "Invalid genre title: EMPTY")
    @NotNull(message = "Invalid genre title: NULL")
    @Pattern(regexp = TITLE, message = "Invalid genre title")
    @Schema(example = "Satire")
    String title;
    @Pattern(regexp = DESCRIPTION, message = "Invalid genre description")
    @Schema(example = "Satire is a genre of the visual, literary, and performing arts.")
    String description;

    public GenreReq(@NotNull(message = "Invalid genre title: NULL") String title) {
        this.title = title;
    }
}
