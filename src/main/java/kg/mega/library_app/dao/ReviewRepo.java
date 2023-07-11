package kg.mega.library_app.dao;

import kg.mega.library_app.models.entities.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepo extends JpaRepository<Review, Long> {
    List<Review> findAllByBook_Id(Long id);
}
