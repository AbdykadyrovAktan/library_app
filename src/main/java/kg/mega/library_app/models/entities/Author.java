package kg.mega.library_app.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PRIVATE;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = PRIVATE)

@Entity
@Table(name = "authors")
public class Author implements Serializable {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    Long id;
    @Column(length = 50)
    String firstname;
    @Column(length = 50)
    String lastname;
    @Column(name = "date_of_birth")
    LocalDate dateOfBirth;
    @Column(length = 50)
    String birthplace;
    @OneToMany(mappedBy = "author", cascade = ALL, orphanRemoval = true)
    List<Book> books;

    public Author(String firstname, String lastname) {
        this.firstname = firstname;
        this.lastname = lastname;
    }
}
