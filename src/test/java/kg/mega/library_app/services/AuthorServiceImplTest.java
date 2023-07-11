package kg.mega.library_app.services;

import kg.mega.library_app.common.exceptions.DataNotFoundException;
import kg.mega.library_app.common.exceptions.DuplicateException;
import kg.mega.library_app.dao.AuthorRepo;
import kg.mega.library_app.models.dto.requests.AuthorReq;
import kg.mega.library_app.models.entities.Author;
import kg.mega.library_app.services.impl.AuthorServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

class AuthorServiceImplTest {
    @Mock
    private AuthorRepo authorRepo;

    @InjectMocks
    private AuthorServiceImpl authorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllByFirstNameAndLastName() {
        String authorFirstname = "John";
        String authorLastname = "Doe";
        List<Author> expectedAuthors = new ArrayList<>();
        expectedAuthors.add(new Author("John", "Doe"));
        when(authorRepo.findAllByFirstnameContainingOrLastnameContaining(authorFirstname, authorLastname))
                .thenReturn(expectedAuthors);

        List<Author> actualAuthors = authorService.getAllByFirstNameAndLastName(authorFirstname, authorLastname);

        Assertions.assertEquals(expectedAuthors, actualAuthors);
        verify(authorRepo, times(1))
                .findAllByFirstnameContainingOrLastnameContaining(authorFirstname, authorLastname);
    }

    @Test
    void testCreateAuthor() throws DuplicateException {
        AuthorReq req = new AuthorReq("John", "Doe", LocalDate.of(1980, 1, 1), "New York");
        Author expectedAuthor = new Author("John", "Doe");
        when(authorRepo.findByFirstnameAndLastname(req.getAuthorFirstname(), req.getAuthorLastname()))
                .thenReturn(null);
        when(authorRepo.save(any(Author.class))).thenReturn(expectedAuthor);

        Author actualAuthor = authorService.createAuthor(req);

        Assertions.assertEquals(expectedAuthor, actualAuthor);
        verify(authorRepo, times(1)).findByFirstnameAndLastname(req.getAuthorFirstname(), req.getAuthorLastname());
        verify(authorRepo, times(1)).save(any(Author.class));
    }

    @Test
    void testCreateAuthorThrowsDuplicateException() {
        AuthorReq req = new AuthorReq("John", "Doe", LocalDate.of(1980, 1, 1), "New York");
        when(authorRepo.findByFirstnameAndLastname(req.getAuthorFirstname(), req.getAuthorLastname()))
                .thenReturn(new Author("John", "Doe"));

        Assertions.assertThrows(DuplicateException.class, () -> authorService.createAuthor(req));
        verify(authorRepo, times(1)).findByFirstnameAndLastname(req.getAuthorFirstname(), req.getAuthorLastname());
        verify(authorRepo, never()).save(any(Author.class));
    }

    @Test
    void testUpdateAuthor() throws DataNotFoundException {
        Long authorId = 1L;
        AuthorReq req = new AuthorReq("John", "Doe", LocalDate.of(1980, 1, 1), "New York");
        Author author = new Author("Jane", "Smith");
        when(authorRepo.findById(authorId)).thenReturn(Optional.of(author));
        when(authorRepo.save(any(Author.class))).thenReturn(author);

        String result = authorService.updateAuthor(authorId, req);

        Assertions.assertEquals("Author with id: 1 successfully updated!", result);
        Assertions.assertEquals(req.getAuthorFirstname(), author.getFirstname());
        Assertions.assertEquals(req.getAuthorLastname(), author.getLastname());
        Assertions.assertEquals(req.getAuthorBirthplace(), author.getBirthplace());
        Assertions.assertEquals(req.getAuthorDateOfBirth(), author.getDateOfBirth());
        verify(authorRepo, times(1)).findById(authorId);
        verify(authorRepo, times(1)).save(author);
    }

    @Test
    void testUpdateAuthorThrowsDataNotFoundException() {
        Long authorId = 1L;
        AuthorReq req = new AuthorReq("John", "Doe", LocalDate.of(1980, 1, 1), "New York");
        when(authorRepo.findById(authorId)).thenReturn(Optional.empty());

        Assertions.assertThrows(DataNotFoundException.class, () -> authorService.updateAuthor(authorId, req));
        verify(authorRepo, times(1)).findById(authorId);
        verify(authorRepo, never()).save(any(Author.class));
    }

    @Test
    void testGetByFirstNameAndLastName() {
        String authorFirstname = "John";
        String authorLastname = "Doe";
        Author expectedAuthor = new Author("John", "Doe");
        when(authorRepo.findByFirstnameAndLastname(authorFirstname, authorLastname)).thenReturn(expectedAuthor);

        Author actualAuthor = authorService.getByFirstNameAndLastName(authorFirstname, authorLastname);

        Assertions.assertEquals(expectedAuthor, actualAuthor);
        verify(authorRepo, times(1)).findByFirstnameAndLastname(authorFirstname, authorLastname);
    }

    @Test
    void testDeleteAuthor() throws DataNotFoundException {
        Long authorId = 1L;
        Author author = new Author("John", "Doe");
        when(authorRepo.findById(authorId)).thenReturn(Optional.of(author));

        String result = authorService.deleteAuthor(authorId);

        Assertions.assertEquals("Author with id: 1 deleted successfully", result);
        verify(authorRepo, times(1)).findById(authorId);
        verify(authorRepo, times(1)).delete(author);
    }

    @Test
    void testDeleteAuthorThrowsDataNotFoundException() {
        Long authorId = 1L;
        when(authorRepo.findById(authorId)).thenReturn(Optional.empty());

        Assertions.assertThrows(DataNotFoundException.class, () -> authorService.deleteAuthor(authorId));
        verify(authorRepo, times(1)).findById(authorId);
        verify(authorRepo, never()).delete(any(Author.class));
    }
}