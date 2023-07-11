package kg.mega.library_app.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PRIVATE;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = PRIVATE)

@Entity
@Table(name = "genres")
public class Genre implements Serializable {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    Long id;
    @Column(length = 50)
    String title;
    @Column(length = 500)
    String description;
    @ManyToMany(mappedBy = "genres", fetch = FetchType.LAZY, cascade = ALL)
    Set<Book> books = new HashSet<>();

    public Genre(String title) {
        this.title = title;
    }

    public Genre(String title, String description) {
        this.title = title;
        this.description = description;
    }
}
