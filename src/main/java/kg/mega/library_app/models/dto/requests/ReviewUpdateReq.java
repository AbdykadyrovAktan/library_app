package kg.mega.library_app.models.dto.requests;

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
public class ReviewUpdateReq {
    @Pattern(regexp = COMMENT, message = "Invalid review text!")
    @Schema(example = "That's book so boring !!!")
    String comment;
}
