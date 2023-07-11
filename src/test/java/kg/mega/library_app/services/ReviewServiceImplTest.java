package kg.mega.library_app.services;

import kg.mega.library_app.common.exceptions.DataNotFoundException;
import kg.mega.library_app.common.exceptions.InadmissibleEditingException;
import kg.mega.library_app.dao.ReviewRepo;
import kg.mega.library_app.models.dto.requests.ReviewReq;
import kg.mega.library_app.models.dto.requests.ReviewUpdateReq;
import kg.mega.library_app.models.dto.responses.ReviewResp;
import kg.mega.library_app.models.entities.Book;
import kg.mega.library_app.models.entities.Review;
import kg.mega.library_app.models.entities.User;
import kg.mega.library_app.services.BookService;
import kg.mega.library_app.services.impl.ReviewServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReviewServiceImplTest {
    @Mock
    private ReviewRepo reviewRepo;

    @Mock
    private BookService bookService;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateReview() throws DataNotFoundException {
        ReviewReq req = new ReviewReq();
        req.setComment("Great book");
        req.setBookId(1L);

        Book book = new Book();
        book.setId(1L);

        User user = new User();
        user.setFirstname("John");
        user.setLastname("Doe");

        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getPrincipal()).thenReturn(user);

        when(bookService.getById(1L)).thenReturn(book);

        Review review = new Review();
        review.setComment(req.getComment());
        review.setBook(book);
        review.setUser(user);

        when(reviewRepo.save(review)).thenReturn(review);

        ReviewResp expectedResp = new ReviewResp(req.getComment(), user.getFirstname(), user.getLastname());

        ReviewResp actualResp = reviewService.createReview(req);

        assertNotNull(actualResp);
        assertEquals(expectedResp.getComment(), actualResp.getComment());
        assertEquals(expectedResp.getUserFirstname(), actualResp.getUserFirstname());
        assertEquals(expectedResp.getUserLastname(), actualResp.getUserLastname());

        verify(bookService, times(1)).getById(1L);
        verify(reviewRepo, times(1)).save(review);
        verifyNoMoreInteractions(bookService, reviewRepo);
    }

    @Test
    void testGetAllReview() {
        List<Review> reviewList = new ArrayList<>();
        reviewList.add(new Review("Great book", new User("John", "Doe")));
        reviewList.add(new Review("Interesting read", new User("Jane", "Smith")));

        when(reviewRepo.findAll()).thenReturn(reviewList);

        List<ReviewResp> expectedRespList = new ArrayList<>();
        expectedRespList.add(new ReviewResp("Great book", "John", "Doe"));
        expectedRespList.add(new ReviewResp("Interesting read", "Jane", "Smith"));

        List<ReviewResp> actualRespList = reviewService.getAllReview();

        assertNotNull(actualRespList);
        assertEquals(expectedRespList.size(), actualRespList.size());
        for (int i = 0; i < expectedRespList.size(); i++) {
            ReviewResp expectedResp = expectedRespList.get(i);
            ReviewResp actualResp = actualRespList.get(i);
            assertEquals(expectedResp.getComment(), actualResp.getComment());
            assertEquals(expectedResp.getUserFirstname(), actualResp.getUserFirstname());
            assertEquals(expectedResp.getUserLastname(), actualResp.getUserLastname());
        }

        verify(reviewRepo, times(1)).findAll();
        verifyNoMoreInteractions(reviewRepo);
    }

    @Test
    void testUpdateReview() throws DataNotFoundException, InadmissibleEditingException {
        Long reviewId = 1L;
        ReviewUpdateReq req = new ReviewUpdateReq();
        req.setComment("Updated review");

        User loggedInUser = new User();
        loggedInUser.setEmail("john@example.com");

        User reviewUser = new User();
        reviewUser.setEmail("john@example.com");

        Review review = new Review();
        review.setId(reviewId);
        review.setComment("Initial review");
        review.setUser(reviewUser);

        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getPrincipal()).thenReturn(loggedInUser);

        when(reviewRepo.findById(reviewId)).thenReturn(Optional.of(review));

        Review updatedReview = new Review();
        updatedReview.setId(reviewId);
        updatedReview.setComment(req.getComment());
        updatedReview.setUser(reviewUser);

        when(reviewRepo.save(updatedReview)).thenReturn(updatedReview);

        ReviewResp expectedResp = new ReviewResp(req.getComment(), reviewUser.getFirstname(), reviewUser.getLastname());

        ReviewResp actualResp = reviewService.updateReview(reviewId, req);

        assertNotNull(actualResp);
        assertEquals(expectedResp.getComment(), actualResp.getComment());
        assertEquals(expectedResp.getUserFirstname(), actualResp.getUserFirstname());
        assertEquals(expectedResp.getUserLastname(), actualResp.getUserLastname());

        verify(reviewRepo, times(1)).findById(reviewId);
        verify(reviewRepo, times(1)).save(updatedReview);
        verifyNoMoreInteractions(reviewRepo);
    }

    @Test
    void testUpdateReviewThrowsInadmissibleEditingException() {
        Long reviewId = 1L;
        ReviewUpdateReq req = new ReviewUpdateReq();
        req.setComment("Updated review");

        User loggedInUser = new User();
        loggedInUser.setEmail("jane@example.com");

        User reviewUser = new User();
        reviewUser.setEmail("john@example.com");

        Review review = new Review();
        review.setId(reviewId);
        review.setComment("Initial review");
        review.setUser(reviewUser);

        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getPrincipal()).thenReturn(loggedInUser);

        when(reviewRepo.findById(reviewId)).thenReturn(Optional.of(review));

        assertThrows(InadmissibleEditingException.class, () -> reviewService.updateReview(reviewId, req));

        verify(reviewRepo, times(1)).findById(reviewId);
        verifyNoMoreInteractions(reviewRepo);
    }

    @Test
    void testDeleteReview() throws DataNotFoundException, InadmissibleEditingException {
        Long reviewId = 1L;

        User loggedInUser = new User();
        loggedInUser.setEmail("john@example.com");

        User reviewUser = new User();
        reviewUser.setEmail("john@example.com");

        Review review = new Review();
        review.setId(reviewId);
        review.setComment("Great book");
        review.setUser(reviewUser);

        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getPrincipal()).thenReturn(loggedInUser);

        when(reviewRepo.findById(reviewId)).thenReturn(Optional.of(review));

        String expectedMessage = "Review with id: " + reviewId + " deleted successfully!";
        String actualMessage = reviewService.deleteReview(reviewId);

        assertEquals(expectedMessage, actualMessage);

        verify(reviewRepo, times(1)).findById(reviewId);
        verify(reviewRepo, times(1)).delete(review);
        verifyNoMoreInteractions(reviewRepo);
    }

    @Test
    void testDeleteReviewThrowsInadmissibleEditingException() {
        Long reviewId = 1L;

        User loggedInUser = new User();
        loggedInUser.setEmail("jane@example.com");

        User reviewUser = new User();
        reviewUser.setEmail("john@example.com");

        Review review = new Review();
        review.setId(reviewId);
        review.setComment("Great book");
        review.setUser(reviewUser);

        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getPrincipal()).thenReturn(loggedInUser);

        when(reviewRepo.findById(reviewId)).thenReturn(Optional.of(review));

        assertThrows(InadmissibleEditingException.class, () -> reviewService.deleteReview(reviewId));

        verify(reviewRepo, times(1)).findById(reviewId);
        verifyNoMoreInteractions(reviewRepo);
    }

    @Test
    void testGetReviewsByBookId() {
        Long bookId = 1L;

        List<Review> reviewList = new ArrayList<>();
        reviewList.add(new Review("Great book", new User("John", "Doe")));
        reviewList.add(new Review("Interesting read", new User("Jane", "Smith")));

        when(reviewRepo.findAllByBook_Id(bookId)).thenReturn(reviewList);

        List<ReviewResp> expectedRespList = new ArrayList<>();
        expectedRespList.add(new ReviewResp("Great book", "John", "Doe"));
        expectedRespList.add(new ReviewResp("Interesting read", "Jane", "Smith"));

        List<ReviewResp> actualRespList = reviewService.getReviewsByBookId(bookId);

        assertNotNull(actualRespList);
        assertEquals(expectedRespList.size(), actualRespList.size());
        for (int i = 0; i < expectedRespList.size(); i++) {
            ReviewResp expectedResp = expectedRespList.get(i);
            ReviewResp actualResp = actualRespList.get(i);
            assertEquals(expectedResp.getComment(), actualResp.getComment());
            assertEquals(expectedResp.getUserFirstname(), actualResp.getUserFirstname());
            assertEquals(expectedResp.getUserLastname(), actualResp.getUserLastname());
        }

        verify(reviewRepo, times(1)).findAllByBook_Id(bookId);
        verifyNoMoreInteractions(reviewRepo);
    }
}