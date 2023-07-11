package kg.mega.library_app.services.impl;

import kg.mega.library_app.common.exceptions.DataNotFoundException;
import kg.mega.library_app.common.exceptions.DuplicateException;
import kg.mega.library_app.common.exceptions.InadmissibleEditingException;
import kg.mega.library_app.dao.UserRepo;
import kg.mega.library_app.dao.VerificationTokenRepo;
import kg.mega.library_app.models.dto.requests.AuthenticationReq;
import kg.mega.library_app.models.dto.requests.RegistrationReq;
import kg.mega.library_app.models.dto.responses.AuthenticationResp;
import kg.mega.library_app.models.dto.responses.RegistrationInterimResp;
import kg.mega.library_app.models.dto.responses.UserResp;
import kg.mega.library_app.models.entities.Book;
import kg.mega.library_app.models.entities.User;
import kg.mega.library_app.models.entities.VerificationToken;
import kg.mega.library_app.services.BookService;
import kg.mega.library_app.services.JwtService;
import kg.mega.library_app.services.NotificationService;
import kg.mega.library_app.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import static kg.mega.library_app.models.constants.Role.ROLE_ADMIN;
import static kg.mega.library_app.models.constants.Role.ROLE_USER;

@Slf4j

@Service
public class UserServiceImpl implements UserService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final VerificationTokenRepo verificationTokenRepo;
    private final NotificationService notificationService;
    private final BookService bookService;


    @Autowired
    public UserServiceImpl(UserRepo userRepo, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager, VerificationTokenRepo verificationTokenRepo, NotificationService notificationService, BookService bookService) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.verificationTokenRepo = verificationTokenRepo;
        this.notificationService = notificationService;
        this.bookService = bookService;
    }

    @Override
    public RegistrationInterimResp register(RegistrationReq req) throws DuplicateException {
        if (userRepo.findByEmail(req.getEmail()).isPresent()) {
            throw new DuplicateException("User with email: " + req.getEmail() + " already exist!");
        }
        User user = User
                .builder()
                .firstname(req.getFirstname())
                .lastname(req.getLastname())
                .phoneNumber(req.getPhoneNumber())
                .isActive(false)
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .role(ROLE_USER)
                .build();
        userRepo.save(user);
        String jwtToken = jwtService.generateToken(user);
        log.info("User registered: {}", user.getEmail());
        return new RegistrationInterimResp(jwtToken, user);
    }

    @Override
    public AuthenticationResp authenticate(AuthenticationReq req) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        req.getEmail(),
                        req.getPassword()
                ));
        User user = userRepo.findByEmail(req.getEmail())
                .orElseThrow();
        String jwtToken = jwtService.generateToken(user);
        log.info("User authenticated: {}", user.getEmail());
        return new AuthenticationResp(jwtToken);
    }

    @Override
    public List<UserResp> getAllUsers() {
        return userRepo.findAll()
                .stream()
                .map(user -> new UserResp(user.getFirstname(),
                        user.getLastname(),
                        user.getPhoneNumber(),
                        user.isActive(),
                        user.getEmail(),
                        user.getRole()))
                .collect(Collectors.toList());
    }

    @Override
    public String blockUser(Long id) throws DataNotFoundException {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User user = userRepo.findById(id)
                .orElseThrow(() -> new DataNotFoundException("User with id: " + id + " not found!"));

        if (currentUser.getRole() != ROLE_ADMIN) {
            throw new InadmissibleEditingException("Only an admin can block users.");
        }

        if (user.getRole() == ROLE_ADMIN) {
            throw new InadmissibleEditingException("Admins cannot be blocked!");
        }

        if (user.isActive()) {
            notificationService.sendEmailNotification(user.getEmail(), "Attention!!!", "Your account is blocked!");
        }

        boolean isActive = user.isActive();
        user.setActive(!isActive);
        log.info("{} user with id = {}; email: {}", isActive ? "Unblock" : "Block", user.getId(), user.getEmail());
        userRepo.save(user);

        return "User with id: " + id + " is " + (isActive ? "blocked" : "unblocked");
    }

    @Override
    public String editUserRole(Long id) throws DataNotFoundException, InadmissibleEditingException {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User user = userRepo.findById(id)
                .orElseThrow(() -> new DataNotFoundException("User with id: " + id + " not found!"));

        if (currentUser.getRole() != ROLE_ADMIN) {
            throw new InadmissibleEditingException("Only an admin can change role.");
        }

        if (user.getAdminCreatedBy() == null) {
            user.setAdminCreatedBy(currentUser);
        }

        if (!user.getAdminCreatedBy().getEmail().equals(currentUser.getEmail())) {
            throw new InadmissibleEditingException("Only the admin who previously changed this user's role can change the role!");
        }

        user.setRole(user.getRole() == ROLE_USER ? ROLE_ADMIN : ROLE_USER);

        userRepo.save(user);
        log.info("User with id = {}; email: {} has role changed", user.getId(), user.getEmail());

        return "User with id: " + id + " has role changed to " + user.getRole();
    }

    @Override
    public String validateToken(String token) {
        VerificationToken verificationToken = verificationTokenRepo.findByToken(token);
        if (token == null) {
            return "Invalid verification token";
        }
        User user = verificationToken.getUser();
        Calendar calendar = Calendar.getInstance();
        if ((verificationToken.getExpirationTime().getTime() - calendar.getTime().getTime()) <= 0) {
            verificationTokenRepo.delete(verificationToken);
            return "Token already expired";
        }
        user.setActive(true);
        userRepo.save(user);
        return "valid";
    }

    @Override
    public void saveUserVerificationToken(User user, String token) {
        verificationTokenRepo.save(new VerificationToken(token, user));
        log.info("User verification token saved successfully for user with email: {}", user.getUsername());
    }

    @Override
    public String addToFavorites(Long bookId) throws DataNotFoundException, DuplicateException {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Book book = bookService.getById(bookId);

        if (user.getFavorites().contains(book)) {
            throw new DuplicateException("Book already exists in favorites");
        }

        user.getFavorites().add(book);
        userRepo.save(user);

        log.info("Book with ID {} added to favorites for user with ID {}", bookId, user.getId());
        return "Book added to favorites successfully";
    }

    @Override
    public String removeFromFavorites(Long bookId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        user.getFavorites().removeIf(b -> b.getId().equals(bookId));
        userRepo.save(user);

        log.info("Book with ID {} removed from favorites for user with ID {}", bookId, user.getId());
        return "Book removed from favorites successfully";
    }
}