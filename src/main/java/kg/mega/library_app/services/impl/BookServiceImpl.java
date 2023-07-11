package kg.mega.library_app.services.impl;

import kg.mega.library_app.common.exceptions.DataNotFoundException;
import kg.mega.library_app.common.exceptions.DuplicateException;
import kg.mega.library_app.common.exceptions.InadmissibleEditingException;
import kg.mega.library_app.dao.BookRepo;
import kg.mega.library_app.models.dto.requests.*;
import kg.mega.library_app.models.dto.responses.BookResp;
import kg.mega.library_app.models.entities.Author;
import kg.mega.library_app.models.entities.Book;
import kg.mega.library_app.models.entities.Genre;
import kg.mega.library_app.models.entities.User;
import kg.mega.library_app.services.AuthorService;
import kg.mega.library_app.services.BookService;
import kg.mega.library_app.services.GenreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j

@Service
public class BookServiceImpl implements BookService {
    private final BookRepo bookRepo;
    private final AuthorService authorService;
    private final GenreService genreService;

    @Autowired
    public BookServiceImpl(BookRepo bookRepo, AuthorService authorService, GenreService genreService) {
        this.bookRepo = bookRepo;
        this.authorService = authorService;
        this.genreService = genreService;
    }

    @Override
    public List<BookResp> getBookRespByGenre(Long id) throws DataNotFoundException {
        List<BookResp> respList = mapBookList(bookRepo.findAllByGenreId(id));
        if (respList.isEmpty()) {
            throw new DataNotFoundException("Genre not found!");
        }
        return respList;
    }

    @Override
    public List<BookResp> getBooksByAuthor(AuthorFullNameReq req) throws DataNotFoundException {
        List<Author> authors = authorService.getAllByFirstNameAndLastName(req.getAuthorFirstname(), req.getAuthorLastname());
        List<Book> books = new ArrayList<>();
        for (Author author : authors) {
            List<Book> authorBooks = bookRepo.findAllByAuthorFirstnameAndAuthorLastname(author.getFirstname(), author.getLastname());
            books.addAll(authorBooks);
        }
        List<BookResp> respList = mapBookList(books);
        if (respList.isEmpty()) {
            throw new DataNotFoundException("Author not found!");
        }
        return respList;
    }

    @Override
    public Book getById(Long bookId) throws DataNotFoundException {
        return bookRepo
                .findById(bookId)
                .orElseThrow(() -> new DataNotFoundException("Book with id: " + bookId + " not found!"));
    }

    @Override
    public List<BookResp> getBookRespByGenreTitle(String title) throws DataNotFoundException {
        List<Genre> genres = genreService.getAllByTitle(title);
        List<Book> books = new ArrayList<>();
        for (Genre genre : genres) {
            List<Book> genreBook = bookRepo.findAllByGenreTitle(genre.getTitle());
            books.addAll(genreBook);
        }
        List<BookResp> respList = mapBookList(books);
        if (respList.isEmpty()) {
            throw new DataNotFoundException("Genre not found!");
        }
        return respList;
    }

    @Override
    public List<BookResp> getBooksByAuthorId(Long id) throws DataNotFoundException {

        List<BookResp> respList = mapBookList(bookRepo.findAllByAuthorId(id));
        if (respList.isEmpty()) {
            throw new DataNotFoundException("Author not found!");
        }
        return respList;
    }

    @Override
    public Book saveBook(Book book) {
        return bookRepo.save(book);
    }

    private List<BookResp> mapBookList(List<Book> books) {
        return books
                .stream()
                .map(book -> BookResp.builder()
                        .bookTitle(book.getTitle())
                        .authorFirstname(book.getAuthor().getFirstname())
                        .authorLastname(book.getAuthor().getLastname())
                        .quantity(book.getQuantity())
                        .genreTitle(book.getGenres().stream()
                                .map(Genre::getTitle)
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public BookResp createBook(BookReq req) throws DuplicateException {
        if (bookRepo.existsByBookDetailsAndGenreTitlesAndAuthorName
                (
                        req.getTitle(),
                        req.getDescription(),
                        req.getPublicationYear(),
                        req.getQuantity(),
                        req.getGenres()
                                .stream()
                                .map(GenreReq::getTitle)
                                .collect(Collectors.toSet()),
                        req.getAuthor().getAuthorFirstname(),
                        req.getAuthor().getAuthorLastname()
                )) {
            throw new DuplicateException("Such a book already exists!");
        }

        Book book = Book
                .builder()
                .title(req.getTitle())
                .description(req.getDescription())
                .publicationYear(req.getPublicationYear())
                .quantity(req.getQuantity())
                .user((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .build();

        Author author = retrieveAuthor(req.getAuthor());
        book.setAuthor(author);

        Set<Genre> genres = retrieveGenres(req.getGenres());
        book.setGenres(genres);

        bookRepo.save(book);
        log.info("Book '{}' created successfully.", book.getTitle());

        return BookResp
                .builder()
                .bookTitle(book.getTitle())
                .authorFirstname(book.getAuthor().getFirstname())
                .authorLastname(book.getAuthor().getLastname())
                .quantity(book.getQuantity())
                .genreTitle(book.getGenres().stream()
                        .map(Genre::getTitle)
                        .collect(Collectors.toList()))
                .build();
    }

    @Override
    public BookResp updateBookEntirely(Long id, BookReq req) throws DataNotFoundException, DuplicateException, InadmissibleEditingException {
        if (bookRepo.existsByBookDetailsAndGenreTitlesAndAuthorName
                (
                        req.getTitle(),
                        req.getDescription(),
                        req.getPublicationYear(),
                        req.getQuantity(),
                        req.getGenres()
                                .stream()
                                .map(GenreReq::getTitle)
                                .collect(Collectors.toSet()),
                        req.getAuthor().getAuthorFirstname(),
                        req.getAuthor().getAuthorLastname()
                )) {
            throw new DuplicateException("Such a book already exists!");
        }

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Book book = checkBook(id, req.getTitle(), req.getDescription(), req.getPublicationYear(), req.getQuantity());

        if (!user.getEmail().equals(book.getUser().getEmail())) {
            throw new InadmissibleEditingException("You cannot edit this book");
        }

        Author author = retrieveAuthor(req.getAuthor());
        book.setAuthor(author);

        Set<Genre> genres = retrieveGenres(req.getGenres());
        book.setGenres(genres);

        bookRepo.save(book);
        log.info("Book '{}' entirely updated successfully.", book.getTitle());

        return BookResp
                .builder()
                .bookTitle(book.getTitle())
                .authorFirstname(book.getAuthor().getFirstname())
                .authorLastname(book.getAuthor().getLastname())
                .quantity(book.getQuantity())
                .genreTitle(book.getGenres().stream()
                        .map(Genre::getTitle)
                        .collect(Collectors.toList()))
                .build();
    }

    @Override
    public BookResp updateBookDetails(Long id, BookDetailsReq req) throws DataNotFoundException, InadmissibleEditingException, DuplicateException {
        if (bookRepo.existsByTitleAndDescriptionAndPublicationYearAndQuantity
                (
                        req.getTitle(),
                        req.getDescription(),
                        req.getPublicationYear(),
                        req.getQuantity()
                )) {
            throw new DuplicateException("Such a book already exists!");
        }

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Book book = checkBook(id, req.getTitle(), req.getDescription(), req.getPublicationYear(), req.getQuantity());

        if (!user.getEmail().equals(book.getUser().getEmail())) {
            throw new InadmissibleEditingException("You cannot edit this book");
        }

        bookRepo.save(book);
        log.info("Book '{}' details updated successfully.", book.getTitle());

        return BookResp
                .builder()
                .bookTitle(book.getTitle())
                .authorFirstname(book.getAuthor().getFirstname())
                .authorLastname(book.getAuthor().getLastname())
                .quantity(book.getQuantity())
                .genreTitle(book.getGenres().stream()
                        .map(Genre::getTitle)
                        .collect(Collectors.toList()))
                .build();
    }

    @Override
    public String deleteBook(Long id) throws DataNotFoundException, InadmissibleEditingException {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Book book = bookRepo
                .findById(id)
                .orElseThrow(() -> new DataNotFoundException("Book with id: " + id + " not found!"));

        if (!user.getEmail().equals(book.getUser().getEmail())) {
            throw new InadmissibleEditingException("You cannot delete this book");
        }

        bookRepo.deleteById(book.getId());
        log.info("Book with id: {} deleted successfully", id);

        return "Book with id: " + id + " deleted successfully";
    }

    @Override
    public List<BookResp> getAllBooks(Integer page, Integer size) {
        return mapBookList(bookRepo.findAll(PageRequest.of(page, size)).getContent());
    }

    private Book checkBook(Long id, String title, String description, String publicationYear, Integer quantity)
            throws DataNotFoundException {
        Book book = bookRepo
                .findById(id)
                .orElseThrow(() -> new DataNotFoundException("Book with id: " + id + " not found!"));

        book.setTitle(title);
        book.setDescription(description);
        book.setPublicationYear(publicationYear);
        book.setQuantity(quantity);
        return book;
    }

    private Author retrieveAuthor(AuthorReq req) throws DuplicateException {
        Author author = authorService
                .getByFirstNameAndLastName(req.getAuthorFirstname(), req.getAuthorLastname());
        if (author == null) {
            log.info("Author '{} {}' not found. Creating a new author.",
                    req.getAuthorFirstname(),
                    req.getAuthorLastname());
            author = authorService.createAuthor(req);
        }
        return author;
    }

    private Set<Genre> retrieveGenres(Set<GenreReq> reqList) throws DuplicateException {
        Set<Genre> genres = new HashSet<>();
        for (GenreReq genreReq : reqList) {
            Genre genre = genreService.getByTitle(genreReq.getTitle());
            if (genre == null) {
                log.info("Genre '{}' not found. Creating a new genre.", genreReq.getTitle());
                genre = genreService.createGenre(genreReq);
            }
            genres.add(genre);
        }
        return genres;
    }
}