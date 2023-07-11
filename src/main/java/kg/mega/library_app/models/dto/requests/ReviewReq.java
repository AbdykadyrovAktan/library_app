package kg.mega.library_app.models.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import static kg.mega.library_app.models.constants.RegExp.COMMENT;
import static lombok.AccessLevel.PRIVATE;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
public class ReviewReq {
    @JsonProperty("book_id")
    @Schema(example = "3")
    Long bookId;
    @Pattern(regexp = COMMENT, message = "Invalid review text!")
    @Schema(example = "Excellent and interesting book !!!")
    String comment;
}
