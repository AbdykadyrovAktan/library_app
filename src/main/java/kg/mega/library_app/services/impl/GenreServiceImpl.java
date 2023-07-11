package kg.mega.library_app.services.impl;

import kg.mega.library_app.common.exceptions.DataNotFoundException;
import kg.mega.library_app.common.exceptions.DuplicateException;
import kg.mega.library_app.dao.GenreRepo;
import kg.mega.library_app.models.dto.requests.GenreReq;
import kg.mega.library_app.models.entities.Genre;
import kg.mega.library_app.services.GenreService;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PSQLException;
import org.postgresql.util.PSQLState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j

@Service
public class GenreServiceImpl implements GenreService {
    private final GenreRepo genreRepo;

    @Autowired
    public GenreServiceImpl(GenreRepo genreRepo) {
        this.genreRepo = genreRepo;
    }

    @Override
    public Genre getByTitle(String title) {
        return genreRepo.findByTitle(title);
    }

    @Override
    public Genre createGenre(GenreReq genreReq) throws DuplicateException {
        if (genreRepo.findByTitle(genreReq.getTitle()) != null) {
            throw new DuplicateException("Genre: " + genreReq.getTitle() + " already exist!");
        }
        return genreRepo.save(Genre
                .builder()
                .title(genreReq.getTitle())
                .description(genreReq.getDescription())
                .build());
    }

    @Override
    public String updateGenre(Long id, GenreReq req) throws DataNotFoundException {
        Genre genre = genreRepo
                .findById(id)
                .orElseThrow(() -> new DataNotFoundException("Genre with id: " + id + " not found!"));
        genre.setTitle(req.getTitle());
        genre.setDescription(req.getDescription());
        genreRepo.save(genre);
        log.info("Genre '{}' updated successfully.", genre.getTitle());
        return "Genre with id: " + id + " updated successfully!";
    }

    @Override
    public String deleteGenre(Long id) throws DataNotFoundException, PSQLException {
        Genre genre = genreRepo
                .findById(id)
                .orElseThrow(() -> new DataNotFoundException("Genre with id: " + id + " not found!"));

        try {
            genreRepo.delete(genre);
            log.info("Genre with id: {} deleted successfully", id);
            return "Genre with id: " + id + " deleted successfully";
        } catch (Exception ex) {
            throw new PSQLException("It is not possible to delete a genre. " +
                    "The genre is associated with several books. " +
                    "To delete this genre, delete all related books.",
                    PSQLState.FOREIGN_KEY_VIOLATION,
                    ex.getCause());
        }
    }

    @Override
    public List<Genre> getAllByTitle(String title) {
        return genreRepo.findAllByTitleContaining(title);
    }
}
