package kg.mega.library_app.services;

import kg.mega.library_app.common.exceptions.DataNotFoundException;
import kg.mega.library_app.common.exceptions.DuplicateException;
import kg.mega.library_app.models.dto.requests.GenreReq;
import kg.mega.library_app.models.entities.Genre;
import org.postgresql.util.PSQLException;

import java.util.List;

public interface GenreService {
    Genre getByTitle(String title);

    Genre createGenre(GenreReq genreReq) throws DuplicateException;

    String updateGenre(Long id, GenreReq req) throws DataNotFoundException;

    String deleteGenre(Long id) throws DataNotFoundException, PSQLException;

    List<Genre> getAllByTitle(String title);
}
