package kg.mega.library_app.services;

import kg.mega.library_app.common.exceptions.DataNotFoundException;
import kg.mega.library_app.common.exceptions.DuplicateException;
import kg.mega.library_app.models.dto.requests.AuthorReq;
import kg.mega.library_app.models.entities.Author;

import java.util.List;

public interface AuthorService {
    List<Author> getAllByFirstNameAndLastName(String authorFirstname, String authorLastname);

    Author createAuthor(AuthorReq req) throws DuplicateException;

    String updateAuthor(Long id, AuthorReq req) throws DataNotFoundException;

    Author getByFirstNameAndLastName(String authorFirstname, String authorLastname);

    String deleteAuthor(Long id) throws DataNotFoundException;
}
