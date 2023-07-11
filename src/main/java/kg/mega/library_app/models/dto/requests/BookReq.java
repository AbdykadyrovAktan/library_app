package kg.mega.library_app.models.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Set;

import static kg.mega.library_app.models.constants.RegExp.*;
import static lombok.AccessLevel.PRIVATE;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = PRIVATE)
public class BookReq {
    @NotBlank(message = "Invalid book title: EMPTY")
    @NotNull(message = "Invalid book title: NULL")
    @Pattern(regexp = TITLE, message = "Invalid book title")
    @Schema(example = "A Clockwork Orange")
    String title;
    @Pattern(regexp = DESCRIPTION, message = "Invalid book description")
    @Schema(example = "A Clockwork Orange is a dystopian satirical black comedy novel.")
    String description;
    @JsonProperty("publication_year")
    @Pattern(regexp = PUBLICATION_YEAR, message = "Invalid book publication year")
    @NotBlank(message = "Invalid publication year: EMPTY")
    @NotNull(message = "Invalid publication year: NULL")
    @Schema(example = "1962")
    String publicationYear;
    @NotNull(message = "Invalid author: NULL")
    AuthorReq author;
    @NotNull(message = "Invalid genres: NULL")
    Set<GenreReq> genres;
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be greater than or equal to 1")
    @Schema(example = "3")
    Integer quantity;
}
