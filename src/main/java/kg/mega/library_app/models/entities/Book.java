package kg.mega.library_app.models.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static jakarta.persistence.CascadeType.*;
import static jakarta.persistence.FetchType.EAGER;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PRIVATE;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = PRIVATE)

@Entity
@Table(name = "books")
public class Book implements Serializable {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    Long id;
    @Column(length = 100)
    String title;
    @Column(length = 500)
    String description;
    @Column(name = "publication_year")
    String publicationYear;
    @Column(nullable = false)
    Integer quantity;
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    User user;

    @ManyToOne(cascade = {PERSIST, MERGE, REFRESH, REMOVE})
    @JoinColumn(name = "author_id", referencedColumnName = "id")
    Author author;

    @ManyToMany(fetch = LAZY)
    @JoinTable(
            name = "book_genre",
            joinColumns = {
                    @JoinColumn(name = "book_id", referencedColumnName = "id", nullable = false)},
            inverseJoinColumns = {
                    @JoinColumn(name = "genre_id", referencedColumnName = "id", nullable = false)}
    )
    Set<Genre> genres = new HashSet<>();
    @OneToMany(mappedBy = "book", cascade = ALL, orphanRemoval = true)
    List<Review> reviews;
    @ManyToMany(fetch = EAGER, mappedBy = "favorites")
    List<User> userFavorites;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Book other = (Book) obj;
        return Objects.equals(id, other.id);
    }

    public Book(Long id) {
        this.id = id;
    }

    public Book(String title) {
        this.title = title;
    }

    public Book(String title, Author author) {
        this.title = title;
        this.author = author;
    }

    public Book(String title, Set<Genre> genres) {
        this.title = title;
        this.genres = genres;
    }
}