package kg.mega.library_app.controllers.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kg.mega.library_app.common.exceptions.DataNotFoundException;
import kg.mega.library_app.common.exceptions.DuplicateException;
import kg.mega.library_app.models.dto.requests.AuthorFullNameReq;
import kg.mega.library_app.models.dto.requests.BookDetailsReq;
import kg.mega.library_app.models.dto.requests.BookReq;
import kg.mega.library_app.services.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/books")
@Validated
@Tag(name = "Book controller")
public class BookController {
    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @Operation(summary = "Get book list",
            description = """
                    Этот метод возвращает список объектов BookResp всех книг которые есть в библиотеке.
                    
                    Также этот метод использует пагинацию и выводит по 5 книг за раз.
                    
                    This method returns a list of BookResp objects of all the books in the library.
                    
                    This method also uses pagination and outputs 5 books at a time.
                    """)
    @GetMapping("/all")
    public ResponseEntity<?> getAllBooks(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "5") Integer size) {
        return ResponseEntity.ok(bookService.getAllBooks(page, size));
    }

    @Operation(summary = "Get books based on genre ID",
            description = """
                    Этот метод возвращает список объектов BookResp всех книг которые связаны с жанром.
                    
                    Поиск производится по ID жанра.
                    
                    Если ID жанра не найдено метод выбрасывает DataNotFoundException.
                    
                    This method returns a list of BookResp objects of all books that are associated with a genre.
                    
                    The search is done by genre ID.
                    
                    If no genre ID is found the method throws DataNotFoundException.
                    """)
    @SecurityRequirement(name = "JWT")
    @GetMapping("/by_genre_id/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> getBooksByGenreId(@PathVariable Long id) throws DataNotFoundException {
        return ResponseEntity.ok(bookService.getBookRespByGenre(id));
    }

    @Operation(summary = "Get books by genre's title",
            description = """
                    Этот метод возвращает список объектов BookResp всех книг которые связаны с жанром.
                    
                    Поиск производится по названию жанра.
                    
                    Если название жанра не найдено метод выбрасывает DataNotFoundException.
                    
                    This method returns a list of BookResp objects of all books that are associated with a genre.
                    
                    The search is done by genre name.
                    
                    If no genre name is found the method throws DataNotFoundException.
                    """)
    @SecurityRequirement(name = "JWT")
    @GetMapping("by_genre_title/{title}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> getBooksByGenreTitle(@PathVariable String title) throws DataNotFoundException {
        return ResponseEntity.ok(bookService.getBookRespByGenreTitle(title));
    }

    @Operation(summary = "Get books by author ID",
            description = """
                    Этот метод возвращает список объектов BookResp всех книг которые связаны с автором.
                    
                    Поиск производится по ID автора.
                    
                    Если ID автора не найдено метод выбрасывает DataNotFoundException.
                    
                    This method returns a list of BookResp objects of all books that are associated with the author.
                    
                    The search is done by author ID.
                    
                    If no author ID is found the method throws a DataNotFoundException.
                    """)
    @SecurityRequirement(name = "JWT")
    @GetMapping("by_author_id/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> getBooksByAuthorId(@PathVariable Long id) throws DataNotFoundException {
        return ResponseEntity.ok(bookService.getBooksByAuthorId(id));
    }

    @Operation(summary = "Get author's books",
            description = """
                    Этот метод возвращает список объектов BookResp всех книг которые связаны с автором.
                    
                    Поиск производится по имени и фамилии автора.
                    
                    Если по имени или фамилии автора ничего не найдено метод выбрасывает DataNotFoundException.
                    
                    This method returns a list of BookResp objects of all books that are associated with the author.
                    
                    The search is done by the author's first and last name.
                    
                    If nothing is found by author's name or surname, the method throws DataNotFoundException.
                    """)
    @SecurityRequirement(name = "JWT")
    @GetMapping("/by_author_name")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> getBooksByAuthorName(@Valid @RequestBody AuthorFullNameReq req) throws DataNotFoundException {
        return ResponseEntity.ok(bookService.getBooksByAuthor(req));
    }

    @Operation(summary = "Add new book",
            description = """
                    Этот метод принимает данные о книге через объект BookReq и создаёт объект Book в базе данных.
                    
                    Так как в библиотеке обычно просто так не добавляют жанр или автора.
                    
                    Этот метод также принимает жанр и автора книги.
                    
                    Если такой жанр или книга уже существуют в библиотеке, то книга связывается с ними.
                    
                    А если нет, то создаются объекты жанра и автора.
                    
                    Метод сравнивает все поля книги, автора и жанров введённые юзером, если какой то объект уже существует, то выбрасывает DuplicateException.
                    
                    Все поля книги валидируются.
                    
                    This method takes book data through a BookReq object and creates a Book object in the database.
                    
                    Since the library doesn't usually just add a genre or author.
                    
                    This method also accepts the genre and author of the book.
                    
                    If such a genre or book already exists in the library, the book is linked to it.
                    
                    If not, the genre and author objects are created.
                    
                    The method compares all book, author and genre fields entered by the user, and if some object already exists, it throws DuplicateException.
                    
                    All fields in the book are validated.
                    """)
    @SecurityRequirement(name = "JWT")
    @PostMapping("/create")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> createBook(@Valid @RequestBody BookReq req) throws DuplicateException {
        return ResponseEntity.status(CREATED).body(bookService.createBook(req));
    }

    @Operation(summary = "Update entire book",
            description = """
                    Этот метод принимает ID книги, все данные о книге через объект BookReq.
                    
                    Обновляет все данные объекта Book в базе данных.
                    
                    Метод сравнивает все поля книги введённые юзером, если такая книга уже существует, то выбрасывает DuplicateException.
                    
                    Если книгу пытается изменить админ, который не создавал его, метод выбрасывает InadmissibleEditingException.
                    
                    Если ID книги не найдено, то метод выбрасывает DataNotFoundException.
                    
                    This method accepts the book ID, all data about the book through the BookReq object.
                    
                    Updates all data of the Book object in the database.
                    
                    The method compares all fields of the book entered by the user, and if such a book already exists, it throws DuplicateException.
                    
                    If an admin who did not create the book tries to change it, the method throws an InadmissibleEditingException.
                    
                    If no book ID is found, the method throws DataNotFoundException.
                    """)
    @SecurityRequirement(name = "JWT")
    @PutMapping("/update/entire/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateBookEntirely(@PathVariable Long id, @Valid @RequestBody BookReq req) throws DataNotFoundException, DuplicateException {
        return ResponseEntity.ok(bookService.updateBookEntirely(id, req));
    }

    @Operation(summary = "Update book details",
            description = """
                    Этот метод принимает ID книги, базовые данные о книге через объект BookDetailsReq.
                    
                    Обновляет базовые данные объекта Book в базе данных.
                    
                    Метод сравнивает все поля книги введённые юзером, если такая книга уже существует, то выбрасывает DuplicateException.
                    
                    Если книгу пытается изменить админ, который не создавал его, метод выбрасывает InadmissibleEditingException.
                    
                    Если ID книги не найдено, то метод выбрасывает DataNotFoundException.
                    
                    This method accepts the book ID, basic data about the book through the BookDetailsReq object.
                    
                    Updates the basic data of the Book object in the database.
                    
                    The method compares all fields of the book entered by the user, and if such a book already exists, it throws DuplicateException.
                    
                    If an admin who did not create the book tries to change it, the method throws an InadmissibleEditingException.
                    
                    If no book ID is found, the method throws DataNotFoundException.
                    """)
    @SecurityRequirement(name = "JWT")
    @PutMapping("/update/details/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateBookDetails(@PathVariable Long id, @Valid @RequestBody BookDetailsReq req) throws DataNotFoundException, DuplicateException {
        return ResponseEntity.ok(bookService.updateBookDetails(id, req));
    }

    @Operation(summary = "Delete book record",
            description = """
                    Этот метод принимает ID книги и удаляет его в базе данных.
                    
                    Если ID книги не найдено, то метод выбрасывает DataNotFoundException.
                    
                    This method takes the book ID and deletes it in the database.
                    
                    If no book ID is found, the method throws DataNotFoundException.
                    """)
    @SecurityRequirement(name = "JWT")
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteBook(@PathVariable Long id) throws DataNotFoundException {
        return ResponseEntity.ok(bookService.deleteBook(id));
    }
}