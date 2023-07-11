package kg.mega.library_app.services;

import kg.mega.library_app.common.exceptions.DataNotFoundException;
import kg.mega.library_app.common.exceptions.DuplicateException;
import kg.mega.library_app.common.exceptions.InadmissibleEditingException;
import kg.mega.library_app.models.dto.requests.AuthorFullNameReq;
import kg.mega.library_app.models.dto.requests.BookDetailsReq;
import kg.mega.library_app.models.dto.requests.BookReq;
import kg.mega.library_app.models.dto.responses.BookResp;
import kg.mega.library_app.models.entities.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface BookService {
    BookResp createBook(BookReq req) throws DuplicateException;

    BookResp updateBookEntirely(Long id, BookReq req) throws DataNotFoundException, DuplicateException, InadmissibleEditingException;

    BookResp updateBookDetails(Long id, BookDetailsReq req) throws DataNotFoundException, DuplicateException, InadmissibleEditingException;

    String deleteBook(Long id) throws DataNotFoundException, InadmissibleEditingException;

    List<BookResp> getAllBooks(Integer page, Integer size);

    List<BookResp> getBookRespByGenre(Long id) throws DataNotFoundException;

    List<BookResp> getBooksByAuthor(AuthorFullNameReq req) throws DataNotFoundException;

    Book getById(Long bookId) throws DataNotFoundException;

    List<BookResp> getBookRespByGenreTitle(String title) throws DataNotFoundException;

    List<BookResp> getBooksByAuthorId(Long id) throws DataNotFoundException;

    Book saveBook(Book book);
}
