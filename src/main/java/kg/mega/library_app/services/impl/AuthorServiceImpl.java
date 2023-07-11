package kg.mega.library_app.services.impl;

import kg.mega.library_app.common.exceptions.DataNotFoundException;
import kg.mega.library_app.common.exceptions.DuplicateException;
import kg.mega.library_app.dao.AuthorRepo;
import kg.mega.library_app.models.dto.requests.AuthorReq;
import kg.mega.library_app.models.entities.Author;
import kg.mega.library_app.services.AuthorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j

@Service
public class AuthorServiceImpl implements AuthorService {
    private final AuthorRepo authorRepo;

    @Autowired
    public AuthorServiceImpl(AuthorRepo authorRepo) {
        this.authorRepo = authorRepo;
    }

    @Override
    public List<Author> getAllByFirstNameAndLastName(String authorFirstname, String authorLastname) {
        return authorRepo.findAllByFirstnameContainingOrLastnameContaining(authorFirstname, authorLastname);
    }

    @Override
    public Author createAuthor(AuthorReq req) throws DuplicateException {
        if (authorRepo
                .findByFirstnameAndLastname(req.getAuthorFirstname(), req.getAuthorLastname()) != null) {
            throw new DuplicateException("Author : " + req.getAuthorFirstname() +
                    " " + req.getAuthorLastname() + "already exist!");
        }
        return authorRepo.save(Author
                .builder()
                .firstname(req.getAuthorFirstname())
                .lastname(req.getAuthorLastname())
                .dateOfBirth(req.getAuthorDateOfBirth())
                .birthplace(req.getAuthorBirthplace())
                .build());
    }

    @Override
    public String updateAuthor(Long id, AuthorReq req) throws DataNotFoundException {
        Author author = authorRepo
                .findById(id)
                .orElseThrow(() -> new DataNotFoundException("Author with id: " + id + " not found!"));
        author.setFirstname(req.getAuthorFirstname());
        author.setLastname(req.getAuthorLastname());
        author.setBirthplace(req.getAuthorBirthplace());
        author.setDateOfBirth(req.getAuthorDateOfBirth());
        authorRepo.save(author);
        log.info("Author with id: {} updated successfully!", id);
        return "Author with id: " + id + " successfully updated!";
    }

    @Override
    public Author getByFirstNameAndLastName(String authorFirstname, String authorLastname) {
        return authorRepo.findByFirstnameAndLastname(authorFirstname, authorLastname);
    }

    @Override
    public String deleteAuthor(Long id) throws DataNotFoundException {
        Author author = authorRepo
                .findById(id)
                .orElseThrow(() -> new DataNotFoundException("Author with id: " + id + " not found!"));

        authorRepo.delete(author);
        log.info("Author with id: {} deleted successfully", id);
        return "Author with id: " + id + " deleted successfully";
    }
}
