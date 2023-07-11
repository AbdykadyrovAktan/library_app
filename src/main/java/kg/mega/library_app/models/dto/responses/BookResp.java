package kg.mega.library_app.models.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = PRIVATE)
public class BookResp {
    @JsonProperty("book_title")
    String bookTitle;
    @JsonProperty("author_firstname")
    String authorFirstname;
    @JsonProperty("author_lastname")
    String authorLastname;
    @JsonProperty("genre_title")
    List<String> genreTitle;
    Integer quantity;
}
