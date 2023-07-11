package kg.mega.library_app.models.entities;

import jakarta.persistence.*;
import kg.mega.library_app.models.constants.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDateTime;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PRIVATE;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = PRIVATE)

@Entity
@Table(name = "order_history")
public class OrderHistory implements Serializable {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    Long id;
    @ManyToOne
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    Order order;
    @Enumerated(STRING)
    @Column(name = "status")
    OrderStatus status;
    @Column(name = "action_date")
    LocalDateTime actionDate;
}
