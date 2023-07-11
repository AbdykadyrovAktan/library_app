package kg.mega.library_app.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDate;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PRIVATE;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = PRIVATE)

@Entity
@Table(name = "orders")
public class Order implements Serializable {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    Long id;
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    User user;
    @ManyToOne
    @JoinColumn(name = "book_id", referencedColumnName = "id")
    Book book;
    @Column(name = "order_date")
    LocalDate orderDate;
    @Column(name = "return_due_date")
    LocalDate returnDueDate;
    @Column(name = "actual_return_date")
    LocalDate actualReturnDate;
}