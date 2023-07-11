package kg.mega.library_app.models.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

import static lombok.AccessLevel.PRIVATE;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = PRIVATE)
public class OrderResp {
    @JsonProperty("user_email")
    String userEmail;
    @JsonProperty("book_title")
    String bookTitle;
    @JsonProperty("order_date")
    LocalDate orderDate;
}
