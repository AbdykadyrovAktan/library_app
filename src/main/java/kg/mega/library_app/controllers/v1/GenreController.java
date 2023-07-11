package kg.mega.library_app.controllers.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kg.mega.library_app.common.exceptions.DataNotFoundException;
import kg.mega.library_app.models.dto.requests.GenreReq;
import kg.mega.library_app.services.GenreService;
import org.postgresql.util.PSQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/genres")
@Validated
@Tag(name = "Genre controller")
public class GenreController {
    private final GenreService genreService;

    @Autowired
    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @Operation(summary = "Update genre information",
            description = """
                    Этот метод принимает ID, объект GenreReq и изменяет данные об жанре в базе данных.
                    
                    Если ID жанра не найдено, метод выбрасывает DataNotFoundException.
                    
                    This method takes ID, GenreReq object and changes the genre data in the database.
                    
                    If no genre ID is found, the method throws a DataNotFoundException.
                    """)
    @SecurityRequirement(name = "JWT")
    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateGenre(@PathVariable Long id, @Valid @RequestBody GenreReq req) throws DataNotFoundException {
        return ResponseEntity.ok(genreService.updateGenre(id, req));
    }

    @Operation(summary = "Remove genre",
            description = """
                    Этот метод принимает ID жанра и удаляет его в базе данных.
                    
                    Если ID жанра не найдено, метод выбрасывает DataNotFoundException.
                    
                    This method takes the genre ID and deletes it in the database.
                    
                    If no genre ID is found, the method throws a DataNotFoundException.
                    """)
    @SecurityRequirement(name = "JWT")
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteGenre(@PathVariable Long id) throws DataNotFoundException, PSQLException {
        return ResponseEntity.ok(genreService.deleteGenre(id));
    }
}