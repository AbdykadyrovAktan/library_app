package kg.mega.library_app.controllers.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kg.mega.library_app.common.exceptions.DataNotFoundException;
import kg.mega.library_app.common.exceptions.DuplicateException;
import kg.mega.library_app.events.RegistrationCompleteEvent;
import kg.mega.library_app.models.dto.requests.AuthenticationReq;
import kg.mega.library_app.models.dto.requests.RegistrationReq;
import kg.mega.library_app.models.dto.responses.RegistrationInterimResp;
import kg.mega.library_app.models.dto.responses.RegistrationResp;
import kg.mega.library_app.models.entities.VerificationToken;
import kg.mega.library_app.services.UserService;
import kg.mega.library_app.services.VerificationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/users")
@Validated
@Tag(name = "User controller")
public class UserController {
    private final UserService userService;
    private final VerificationTokenService verificationTokenService;
    private final ApplicationEventPublisher publisher;

    @Autowired
    public UserController(UserService userService, VerificationTokenService verificationTokenService, ApplicationEventPublisher publisher) {
        this.userService = userService;
        this.verificationTokenService = verificationTokenService;
        this.publisher = publisher;
    }

    @Operation(summary = "Register a user",
            description = """
                    Этот метод принимает данные о пользователе, валидирует данные, отправляет на почту сообщение для подтверждения.
                    
                    Если юзер с таким адресом почты уже существует, выбрасывает DuplicateException.
                    
                    This method accepts user data, validates the data, and sends a confirmation message to the email.
                    
                    If the user with that email address already exists, it throws a DuplicateException.
                    """)
    @PostMapping("/registration")
    public ResponseEntity<?> registration(
            @Valid @RequestBody RegistrationReq req,
            final HttpServletRequest request
    ) throws DuplicateException {
        RegistrationInterimResp resp = userService.register(req);
        publisher.publishEvent(new RegistrationCompleteEvent(resp.getUser(), applicationUrl(request)));
        return ResponseEntity.status(CREATED).body(new RegistrationResp(resp.getToken(),
                "Success! Please, check your email to complete your registration."));
    }

    private String applicationUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }

    @Operation(summary = "Verify user email",
            description = """
                    Этот метод подтверждает почту с помощью ValidateToken'а и делает пользователя активным.
                    
                    This method validates the mail with a ValidateToken and makes the user active.
                    """)
    @GetMapping("/verify_email")
    public String verifyEmail(@RequestParam("token") String token) {
        VerificationToken theToken = verificationTokenService.getByToken(token);
        if (theToken.getUser().isEnabled()) {
            return "This account has already been verified, please, login.";
        }
        String verificationResult = userService.validateToken(token);
        if (verificationResult.equalsIgnoreCase("valid")) {
            return "Email verified successfully. Now you can login to your account";
        }
        return "Invalid verification token";
    }

    @Operation(summary = "Identity verification",
            description = """
                    Этот метод реализует аутентификацию пользователя и выдаёт ему AccessToken.
                    
                    This method implements user authentication and gives the user an AccessToken.
                    """)
    @PostMapping("/authentication")
    public ResponseEntity<?> authentication(@Valid @RequestBody AuthenticationReq req) {
        return ResponseEntity.ok(userService.authenticate(req));
    }

    @Operation(summary = "Get user list",
            description = """
                    Этот метод возвращает список UserResp для просматривания информации о пользователях.
                    
                    This method returns the UserResp list for viewing user information.
                    """)
    @SecurityRequirement(name = "JWT")
    @GetMapping("/all")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @Operation(summary = "Disable user account",
            description = """
                    Этот метод реализует блокировку юзера админом и отправляет на почту сообщение.
                    
                    Если тот, кто пытается заблокировать не является админом, выбрасывает InadmissibleEditingException.
                    
                    Если тот кого пытаются заблокировать является админом выбрасывает InadmissibleEditingException.
                    
                    This method implements an admin blocking the user and sends an email message.
                    
                    If the person trying to block is not an admin, it throws an InadmissibleEditingException.
                    
                    If the person they are trying to block is an admin it throws an InadmissibleEditingException.
                    """)
    @SecurityRequirement(name = "JWT")
    @PatchMapping("/block/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> blockUser(@PathVariable Long id) throws DataNotFoundException {
        return ResponseEntity.ok(userService.blockUser(id));
    }

    @Operation(summary = "Change user authority",
            description = """
                    Этот метод реализует изменение роли пользователя админом.
                    
                    Если тот, кто пытается заблокировать не является админом, выбрасывается InadmissibleEditingException.
                    
                    Только админ, который назначал юзера админом ранее, может изменять его роль, иначе выбрасывается InadmissibleEditingException.
                    
                    This method implements the change of the user's role by the administrator.
                    
                    If the person trying to block is not an admin, an InadmissibleEditingException is thrown.
                    
                    Only the admin who assigned the user as admin before can change his role, otherwise an InadmissibleEditingException is thrown.
                    """)
    @SecurityRequirement(name = "JWT")
    @PatchMapping("/edit/role/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> editRole(@PathVariable Long id) throws DataNotFoundException {
        return ResponseEntity.ok(userService.editUserRole(id));
    }

    @Operation(summary = "Add book to favorites",
            description = """
                    Этот метод реализует добавление в избранное книги.
                    
                    Метод принимает ID книги и добавляет в список избранных юзера.
                    
                    Если ID книги не найдено, выбрасывает DataNotFoundException.
                    
                    This method implements adding a book to favorites.
                    
                    The method takes the book ID and adds it to the user's favorites list.
                    
                    If no book ID is found, it throws DataNotFoundException.
                    """)
    @SecurityRequirement(name = "JWT")
    @PatchMapping("/add_to_favorites/{bookId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> addToFavorites(@PathVariable Long bookId) throws DataNotFoundException, DuplicateException {
        return ResponseEntity.ok(userService.addToFavorites(bookId));
    }

    @Operation(summary = "Remove book from favorites",
            description = """
                    Этот метод реализует удаление книги из списка избранных.
                    
                    Метод принимает ID книги и удаляет из списка избранных юзера.
                    
                    Если ID книги не найдено, выбрасывает DataNotFoundException.
                    
                    This method implements removing a book from the favorites list.
                    
                    The method takes book ID and removes from the user's favorites list.
                    
                    If no book ID is found, it throws DataNotFoundException.
                    """)
    @SecurityRequirement(name = "JWT")
    @PatchMapping("/remove_from_favorites/{bookId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> removeFromFavorites(@PathVariable Long bookId) throws DataNotFoundException {
        return ResponseEntity.ok(userService.removeFromFavorites(bookId));
    }
}