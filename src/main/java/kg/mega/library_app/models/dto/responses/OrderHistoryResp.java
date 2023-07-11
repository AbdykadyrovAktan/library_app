package kg.mega.library_app.models.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import kg.mega.library_app.models.constants.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
public class OrderHistoryResp {
    @JsonProperty("order_id")
    Long orderId;
    OrderStatus status;
    @JsonProperty("action_date")
    LocalDateTime actionDate;
}
