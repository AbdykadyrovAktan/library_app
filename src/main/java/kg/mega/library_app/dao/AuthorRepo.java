package kg.mega.library_app.dao;

import kg.mega.library_app.models.entities.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorRepo extends JpaRepository<Author, Long> {
    List<Author> findAllByFirstnameContainingOrLastnameContaining(String firstname, String lastname);

    Author findByFirstnameAndLastname(String firstname, String lastname);
}
