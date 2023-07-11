package kg.mega.library_app.services;

import kg.mega.library_app.common.exceptions.DataNotFoundException;
import kg.mega.library_app.common.exceptions.DuplicateException;
import kg.mega.library_app.dao.BookRepo;
import kg.mega.library_app.models.dto.requests.AuthorFullNameReq;
import kg.mega.library_app.models.dto.requests.AuthorReq;
import kg.mega.library_app.models.dto.requests.BookReq;
import kg.mega.library_app.models.dto.requests.GenreReq;
import kg.mega.library_app.models.dto.responses.BookResp;
import kg.mega.library_app.models.entities.Author;
import kg.mega.library_app.models.entities.Book;
import kg.mega.library_app.models.entities.Genre;
import kg.mega.library_app.models.entities.User;
import kg.mega.library_app.services.AuthorService;
import kg.mega.library_app.services.GenreService;
import kg.mega.library_app.services.impl.BookServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;
import java.util.stream.Collectors;

import static org.mockito.Mockito.*;

class BookServiceImplTest {
    @Mock
    private BookRepo bookRepo;

    @Mock
    private AuthorService authorService;

    @Mock
    private GenreService genreService;

    private BookServiceImpl bookService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        bookService = new BookServiceImpl(bookRepo, authorService, genreService);
    }

    @Test
    void testGetBookRespByGenreThrowsDataNotFoundException() {
        Long genreId = 1L;
        when(bookRepo.findAllByGenreId(genreId)).thenReturn(new ArrayList<>());

        Assertions.assertThrows(DataNotFoundException.class, () -> bookService.getBookRespByGenre(genreId));
        verify(bookRepo, times(1)).findAllByGenreId(genreId);
    }

    @Test
    void testGetBooksByAuthor() throws DataNotFoundException {
        AuthorFullNameReq req = new AuthorFullNameReq("John", "Doe");
        List<Author> authors = new ArrayList<>();
        authors.add(new Author("John", "Doe"));
        when(authorService.getAllByFirstNameAndLastName(req.getAuthorFirstname(), req.getAuthorLastname())).thenReturn(authors);
        List<Book> books = new ArrayList<>();
        books.add(new Book("Book 1", new Author("John", "Doe")));
        books.add(new Book("Book 2", new Author("John", "Doe")));
        when(bookRepo.findAllByAuthorFirstnameAndAuthorLastname("John", "Doe")).thenReturn(books);

        List<BookResp> result = bookService.getBooksByAuthor(req);

        Assertions.assertEquals(2, result.size());
        verify(authorService, times(1)).getAllByFirstNameAndLastName(req.getAuthorFirstname(), req.getAuthorLastname());
        verify(bookRepo, times(1)).findAllByAuthorFirstnameAndAuthorLastname("John", "Doe");
    }

    @Test
    void testGetBooksByAuthorThrowsDataNotFoundException() {
        AuthorFullNameReq req = new AuthorFullNameReq("John", "Doe");
        when(authorService.getAllByFirstNameAndLastName(req.getAuthorFirstname(), req.getAuthorLastname())).thenReturn(new ArrayList<>());

        Assertions.assertThrows(DataNotFoundException.class, () -> bookService.getBooksByAuthor(req));
        verify(authorService, times(1)).getAllByFirstNameAndLastName(req.getAuthorFirstname(), req.getAuthorLastname());
    }

    @Test
    void testGetById() throws DataNotFoundException {
        Long bookId = 1L;
        Book book = new Book("Book 1");
        when(bookRepo.findById(bookId)).thenReturn(Optional.of(book));

        Book result = bookService.getById(bookId);

        Assertions.assertEquals(book, result);
        verify(bookRepo, times(1)).findById(bookId);
    }

    @Test
    void testGetByIdThrowsDataNotFoundException() {
        Long bookId = 1L;
        when(bookRepo.findById(bookId)).thenReturn(Optional.empty());

        Assertions.assertThrows(DataNotFoundException.class, () -> bookService.getById(bookId));
        verify(bookRepo, times(1)).findById(bookId);
    }

    @Test
    void testGetBookRespByGenreTitleThrowsDataNotFoundException() {
        String genreTitle = "Genre 1";
        when(genreService.getAllByTitle(genreTitle)).thenReturn(new ArrayList<>());

        Assertions.assertThrows(DataNotFoundException.class, () -> bookService.getBookRespByGenreTitle(genreTitle));
        verify(genreService, times(1)).getAllByTitle(genreTitle);
    }

    @Test
    void testGetBooksByAuthorId() throws DataNotFoundException {
        Long authorId = 1L;
        List<Book> books = new ArrayList<>();
        books.add(new Book("Book 1", new Author("John", "Doe")));
        books.add(new Book("Book 2", new Author("John", "Doe")));
        when(bookRepo.findAllByAuthorId(authorId)).thenReturn(books);

        List<BookResp> result = bookService.getBooksByAuthorId(authorId);

        Assertions.assertEquals(2, result.size());
        verify(bookRepo, times(1)).findAllByAuthorId(authorId);
    }

    @Test
    void testGetBooksByAuthorIdThrowsDataNotFoundException() {
        Long authorId = 1L;
        when(bookRepo.findAllByAuthorId(authorId)).thenReturn(new ArrayList<>());

        Assertions.assertThrows(DataNotFoundException.class, () -> bookService.getBooksByAuthorId(authorId));
        verify(bookRepo, times(1)).findAllByAuthorId(authorId);
    }

    @Test
    void testSaveBook() {
        Book book = new Book("Book 1");
        when(bookRepo.save(book)).thenReturn(book);

        Book result = bookService.saveBook(book);

        Assertions.assertEquals(book, result);
        verify(bookRepo, times(1)).save(book);
    }

    @Test
    void testCreateBook() throws DuplicateException {
        BookReq req = new BookReq();
        req.setTitle("Book 1");
        req.setDescription("Description");
        req.setPublicationYear("2021");
        req.setQuantity(2);
        Set<GenreReq> genres = new HashSet<>();
        genres.add(new GenreReq("Genre 1"));
        req.setGenres(genres);
        AuthorReq authorReq = new AuthorReq("John", "Doe");
        req.setAuthor(authorReq);
        User user = new User();
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user, null));
        when(bookRepo.existsByBookDetailsAndGenreTitlesAndAuthorName(
                req.getTitle(),
                req.getDescription(),
                req.getPublicationYear(),
                req.getQuantity(),
                req.getGenres().stream().map(GenreReq::getTitle).collect(Collectors.toSet()),
                req.getAuthor().getAuthorFirstname(),
                req.getAuthor().getAuthorLastname()
        )).thenReturn(false);
        when(authorService.getByFirstNameAndLastName(authorReq.getAuthorFirstname(), authorReq.getAuthorLastname())).thenReturn(null);
        when(authorService.createAuthor(authorReq)).thenReturn(new Author("John", "Doe"));
        Genre genre = new Genre("Genre 1");
        when(genreService.getByTitle("Genre 1")).thenReturn(null);
        when(genreService.createGenre(new GenreReq("Genre 1"))).thenReturn(genre);

        BookResp result = bookService.createBook(req);

        Assertions.assertEquals("Book 1", result.getBookTitle());
        Assertions.assertEquals("John", result.getAuthorFirstname());
        Assertions.assertEquals("Doe", result.getAuthorLastname());
        Assertions.assertEquals(2, result.getQuantity());
        Assertions.assertEquals(Collections.singletonList("Genre 1"), result.getGenreTitle());
        verify(bookRepo, times(1)).existsByBookDetailsAndGenreTitlesAndAuthorName(
                req.getTitle(),
                req.getDescription(),
                req.getPublicationYear(),
                req.getQuantity(),
                req.getGenres().stream().map(GenreReq::getTitle).collect(Collectors.toSet()),
                req.getAuthor().getAuthorFirstname(),
                req.getAuthor().getAuthorLastname()
        );
        verify(bookRepo, times(1)).save(any(Book.class));
        verify(authorService, times(1)).getByFirstNameAndLastName(authorReq.getAuthorFirstname(), authorReq.getAuthorLastname());
        verify(authorService, times(1)).createAuthor(authorReq);
        verify(genreService, times(1)).getByTitle("Genre 1");
        verify(genreService, times(1)).createGenre(new GenreReq("Genre 1"));
    }

    @Test
    void testCreateBookThrowsDuplicateException() throws DuplicateException {
        BookReq req = new BookReq();
        req.setTitle("Book 1");
        req.setDescription("Description");
        req.setPublicationYear("2021");
        req.setQuantity(2);
        Set<GenreReq> genres = new HashSet<>();
        genres.add(new GenreReq("Genre 1"));
        req.setGenres(genres);
        AuthorReq authorReq = new AuthorReq("John", "Doe");
        req.setAuthor(authorReq);
        User user = new User();
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user, null));
        when(bookRepo.existsByBookDetailsAndGenreTitlesAndAuthorName(
                req.getTitle(),
                req.getDescription(),
                req.getPublicationYear(),
                req.getQuantity(),
                req.getGenres().stream().map(GenreReq::getTitle).collect(Collectors.toSet()),
                req.getAuthor().getAuthorFirstname(),
                req.getAuthor().getAuthorLastname()
        )).thenReturn(true);

        Assertions.assertThrows(DuplicateException.class, () -> bookService.createBook(req));
        verify(bookRepo, times(1)).existsByBookDetailsAndGenreTitlesAndAuthorName(
                req.getTitle(),
                req.getDescription(),
                req.getPublicationYear(),
                req.getQuantity(),
                req.getGenres().stream().map(GenreReq::getTitle).collect(Collectors.toSet()),
                req.getAuthor().getAuthorFirstname(),
                req.getAuthor().getAuthorLastname()
        );
        verify(bookRepo, times(0)).save(any(Book.class));
        verify(authorService, times(0)).getByFirstNameAndLastName(authorReq.getAuthorFirstname(), authorReq.getAuthorLastname());
        verify(authorService, times(0)).createAuthor(authorReq);
        verify(genreService, times(0)).getByTitle("Genre 1");
        verify(genreService, times(0)).createGenre(new GenreReq("Genre 1"));
    }
}