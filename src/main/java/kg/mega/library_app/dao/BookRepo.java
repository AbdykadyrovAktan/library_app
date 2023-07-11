package kg.mega.library_app.dao;

import kg.mega.library_app.models.entities.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface BookRepo extends JpaRepository<Book, Long> {
    @Query("SELECT b FROM Book b JOIN b.genres g WHERE g.id = ?1")
    List<Book> findAllByGenreId(Long id);

    List<Book> findAllByAuthorFirstnameAndAuthorLastname(String firstname, String lastname);

    @Query("SElECT b FROM Book b join b.genres g WHERE g.title = ?1")
    List<Book> findAllByGenreTitle(String title);

//    @Query("SELECT b FROM Book b JOIN b.author a WHERE a.id = :id")
//    List<Book> findAllByAuthorId(@Param("id") Long id);

    List<Book> findAllByAuthorId(Long id);

    @Query("SELECT COUNT(b) > 0 " +
            "FROM Book b " +
            "JOIN b.genres g " +
            "JOIN b.author a " +
            "WHERE b.title = ?1 " +
            "AND b.description = ?2 " +
            "AND b.publicationYear = ?3 " +
            "AND b.quantity = ?4 " +
            "AND g.title IN ?5 " +
            "AND a.firstname = ?6 " +
            "AND a.lastname = ?7")
    boolean existsByBookDetailsAndGenreTitlesAndAuthorName
            (
                    String bookTitle,
                    String description,
                    String publicationYear,
                    Integer quantity,
                    Set<String> genreTitles,
                    String authorFirstname,
                    String authorLastname
            );

    boolean existsByTitleAndDescriptionAndPublicationYearAndQuantity
            (
                    String title, String description, String publicationYear, Integer quantity
            );
}