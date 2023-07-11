package kg.mega.library_app.services.impl;

import kg.mega.library_app.common.exceptions.DataNotFoundException;
import kg.mega.library_app.common.exceptions.InadmissibleEditingException;
import kg.mega.library_app.dao.ReviewRepo;
import kg.mega.library_app.models.dto.requests.ReviewReq;
import kg.mega.library_app.models.dto.requests.ReviewUpdateReq;
import kg.mega.library_app.models.dto.responses.ReviewResp;
import kg.mega.library_app.models.entities.Review;
import kg.mega.library_app.models.entities.User;
import kg.mega.library_app.services.BookService;
import kg.mega.library_app.services.ReviewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j

@Service
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepo reviewRepo;
    private final BookService bookService;

    @Autowired
    public ReviewServiceImpl(ReviewRepo reviewRepo, BookService bookService) {
        this.reviewRepo = reviewRepo;
        this.bookService = bookService;
    }

    @Override
    public ReviewResp createReview(ReviewReq req) throws DataNotFoundException {
        Review review = Review
                .builder()
                .comment(req.getComment())
                .book(bookService.getById(req.getBookId()))
                .user((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .build();
        reviewRepo.save(review);
        String userFirstname = review.getUser().getFirstname();
        log.info(userFirstname + "'s comment is successfully saved!");
        return new ReviewResp(review.getComment(), userFirstname, review.getUser().getLastname());
    }

    @Override
    public List<ReviewResp> getAllReview() {
        return reviewRepo.findAll()
                .stream()
                .map(review -> new ReviewResp(review.getComment(),
                        review.getUser().getFirstname(), review.getUser().getLastname()))
                .collect(Collectors.toList());
    }

    @Override
    public ReviewResp updateReview(Long id, ReviewUpdateReq req) throws DataNotFoundException, InadmissibleEditingException {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Review review = reviewRepo
                .findById(id)
                .orElseThrow(() -> new DataNotFoundException("Review with id: " + id + " not found!"));

        if (!user.getEmail().equals(review.getUser().getEmail())) {
            throw new InadmissibleEditingException("You cannot edit this review!");
        }

        review.setComment(req.getComment());

        reviewRepo.save(review);
        log.info(review.getUser().getFirstname() + "'s comment is successfully updated!");
        return new ReviewResp(review.getComment(), review.getUser().getFirstname(), review.getUser().getLastname());
    }

    @Override
    public String deleteReview(Long id) throws DataNotFoundException, InadmissibleEditingException {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Review review = reviewRepo
                .findById(id)
                .orElseThrow(() -> new DataNotFoundException("Review with id: " + id + " not found!"));

        if (!user.getEmail().equals(review.getUser().getEmail())) {
            throw new InadmissibleEditingException("You cannot delete this book");
        }

        reviewRepo.delete(review);
        log.info("Review with id: {} deleted successfully!", id);

        return "Review with id: " + id + " deleted successfully!";
    }

    @Override
    public List<ReviewResp> getReviewsByBookId(Long id) {
        List<Review> reviews = reviewRepo.findAllByBook_Id(id);
        return reviews
                .stream()
                .map(review -> new ReviewResp(review.getComment(),
                        review.getUser().getFirstname(),
                        review.getUser().getLastname()))
                .collect(Collectors.toList());
    }
}
