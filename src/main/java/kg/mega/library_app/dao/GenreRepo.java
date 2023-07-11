package kg.mega.library_app.dao;

import kg.mega.library_app.models.entities.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GenreRepo extends JpaRepository<Genre, Long> {
    Genre findByTitle(String title);

    List<Genre> findAllByTitleContaining(String title);
}
