package kg.mega.library_app.controllers.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kg.mega.library_app.common.exceptions.DataNotFoundException;
import kg.mega.library_app.models.dto.requests.AuthorReq;
import kg.mega.library_app.services.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/authors")
@Validated
@Tag(name = "Author controller")
public class AuthorController {
    private final AuthorService authorService;

    @Autowired
    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @Operation(summary = "Update author information",
            description = """
                    Этот метод принимает ID, объект AuthorReq и изменяет данные об авторе в базе данных.
                    
                    Если ID автора не найдено, метод выбрасывает DataNotFoundException.
                    
                    This method takes ID, AuthorReq object and changes the author data in the database.
                    
                    If no author ID is found, the method throws a DataNotFoundException.
                    """)
    @SecurityRequirement(name = "JWT")
    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateAuthor(@PathVariable Long id, @Valid @RequestBody AuthorReq req) throws DataNotFoundException {
        return ResponseEntity.ok(authorService.updateAuthor(id, req));
    }

    @Operation(summary = "Remove author",
            description = """
                    Этот метод принимает ID автора и удаляет его в базе данных.
                    
                    Если ID автора не найдено, метод выбрасывает DataNotFoundException.
                    
                    This method takes the author ID and deletes it in the database.
                    
                    If no author ID is found, the method throws a DataNotFoundException.
                    """)
    @SecurityRequirement(name = "JWT")
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteAuthor(@PathVariable Long id) throws DataNotFoundException {
        return ResponseEntity.ok(authorService.deleteAuthor(id));
    }
}