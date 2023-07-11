package kg.mega.library_app.controllers.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kg.mega.library_app.common.exceptions.DataNotFoundException;
import kg.mega.library_app.common.exceptions.InadmissibleEditingException;
import kg.mega.library_app.models.dto.requests.ReviewReq;
import kg.mega.library_app.models.dto.requests.ReviewUpdateReq;
import kg.mega.library_app.services.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/reviews")
@Validated
@Tag(name = "Review controller")
public class ReviewController {
    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @Operation(summary = "Add new review",
            description = """
                    Этот метод принимает объект ReviewReq и создает объект Review, связывая с конкретной книгой.
                    
                    This method takes a ReviewReq object and creates a Review object, linking to a specific book.
                    """)
    @SecurityRequirement(name = "JWT")
    @PostMapping("/create")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> createReview(@Valid @RequestBody ReviewReq req) throws DataNotFoundException {
        return ResponseEntity.status(CREATED).body(reviewService.createReview(req));
    }

    @Operation(summary = "Get review list",
            description = """
                    Этот метод возвращает список объектов ReviewResp всех существующих отзывов.
                    
                    This method returns a list of ReviewResp objects of all existing reviews.
                    """)
    @SecurityRequirement(name = "JWT")
    @GetMapping("/all")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getAllReviews() {
        return ResponseEntity.ok(reviewService.getAllReview());
    }

    @Operation(summary = "Retrieve book reviews",
            description = """
                    Этот метод возвращает список объектов ReviewResp, связанных с конкретной книгой.
                    
                    This method returns a list of ReviewResp objects associated with a particular book.
                    """)
    @SecurityRequirement(name = "JWT")
    @GetMapping("/by_book/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> getReviewsByBook(@PathVariable Long id) {
        return ResponseEntity.ok(reviewService.getReviewsByBookId(id));
    }

    @Operation(summary = "Update review content",
            description = """
                    Этот метод обновляет отзыв о книге.
                    
                    Если пользователь пытается изменить чужой отзыв, выбрасывается InadmissibleEditingException.
                    
                    Если отзыв по ID не найден, выбрасывается DataNotFoundException.
                    
                    This method updates the book review.
                    
                    If the user tries to change someone else's review, an InadmissibleEditingException is thrown.
                    
                    If the review by ID is not found, a DataNotFoundException is thrown.
                    """)
    @SecurityRequirement(name = "JWT")
    @PatchMapping("/update/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> updateReview(@PathVariable Long id, @Valid @RequestBody ReviewUpdateReq req) throws DataNotFoundException {
        return ResponseEntity.ok(reviewService.updateReview(id, req));
    }

    @Operation(summary = "Delete review entry",
            description = """
                    Этот метод удаляет отзыв о книге.
                    
                    Если пользователь пытается удалить чужой отзыв, выбрасывается InadmissibleEditingException.
                    
                    Если отзыв по ID не найден, выбрасывается DataNotFoundException.
                    
                    This method removes the book review.
                    
                    If the user tries to delete someone else's review, an InadmissibleEditingException is thrown.
                    
                    If the review by ID is not found, a DataNotFoundException is thrown.
                    """)
    @SecurityRequirement(name = "JWT")
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> deleteReview(@PathVariable Long id) throws DataNotFoundException, InadmissibleEditingException {
        return ResponseEntity.ok(reviewService.deleteReview(id));
    }
}