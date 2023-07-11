package kg.mega.library_app.services;

import kg.mega.library_app.common.exceptions.DataNotFoundException;
import kg.mega.library_app.common.exceptions.DuplicateException;
import kg.mega.library_app.models.dto.requests.AuthenticationReq;
import kg.mega.library_app.models.dto.requests.RegistrationReq;
import kg.mega.library_app.models.dto.responses.AuthenticationResp;
import kg.mega.library_app.models.dto.responses.RegistrationInterimResp;
import kg.mega.library_app.models.dto.responses.UserResp;
import kg.mega.library_app.models.entities.User;

import java.util.List;

public interface UserService {
    RegistrationInterimResp register(RegistrationReq req) throws DuplicateException;

    AuthenticationResp authenticate(AuthenticationReq req);

    List<UserResp> getAllUsers();

    String blockUser(Long id) throws DataNotFoundException;

    String editUserRole(Long id) throws DataNotFoundException;

    String validateToken(String token);

    void saveUserVerificationToken(User user, String token);

    String addToFavorites(Long bookId) throws DataNotFoundException, DuplicateException;

    String removeFromFavorites(Long bookId) throws DataNotFoundException;
}
