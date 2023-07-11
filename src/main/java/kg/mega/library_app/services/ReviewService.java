package kg.mega.library_app.services;

import kg.mega.library_app.common.exceptions.DataNotFoundException;
import kg.mega.library_app.common.exceptions.InadmissibleEditingException;
import kg.mega.library_app.models.dto.requests.ReviewReq;
import kg.mega.library_app.models.dto.requests.ReviewUpdateReq;
import kg.mega.library_app.models.dto.responses.ReviewResp;

import java.util.List;

public interface ReviewService {
    ReviewResp createReview(ReviewReq req) throws DataNotFoundException;

    List<ReviewResp> getAllReview();

    ReviewResp updateReview(Long id, ReviewUpdateReq req) throws DataNotFoundException, InadmissibleEditingException;

    String deleteReview(Long id) throws DataNotFoundException, InadmissibleEditingException;

    List<ReviewResp> getReviewsByBookId(Long id);
}
