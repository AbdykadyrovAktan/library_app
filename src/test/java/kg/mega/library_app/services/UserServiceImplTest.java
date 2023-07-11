package kg.mega.library_app.services;

import kg.mega.library_app.common.exceptions.DuplicateException;
import kg.mega.library_app.dao.UserRepo;
import kg.mega.library_app.models.dto.requests.AuthenticationReq;
import kg.mega.library_app.models.dto.requests.RegistrationReq;
import kg.mega.library_app.models.dto.responses.AuthenticationResp;
import kg.mega.library_app.models.dto.responses.RegistrationInterimResp;
import kg.mega.library_app.models.dto.responses.UserResp;
import kg.mega.library_app.models.entities.User;
import kg.mega.library_app.services.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceImplTest {
    @Mock
    private UserRepo userRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register_WithDuplicateEmail_ShouldThrowDuplicateException() {
        RegistrationReq req = new RegistrationReq();
        req.setEmail("test@example.com");

        when(userRepo.findByEmail(req.getEmail())).thenReturn(Optional.of(new User()));

        assertThrows(DuplicateException.class, () -> userService.register(req));

        verify(userRepo, times(1)).findByEmail(req.getEmail());
        verifyNoMoreInteractions(userRepo);
        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(jwtService);
    }

    @Test
    void authenticate_WithValidRequest_ShouldReturnAuthenticationResp() {
        AuthenticationReq req = new AuthenticationReq();
        req.setEmail("test@example.com");
        req.setPassword("password");

        User user = User.builder()
                .id(1L)
                .email(req.getEmail())
                .password("encodedPassword")
                .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(userRepo.findByEmail(req.getEmail())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("jwtToken");

        AuthenticationResp resp = userService.authenticate(req);

        assertNotNull(resp);
        assertEquals("jwtToken", resp.getToken());

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepo, times(1)).findByEmail(req.getEmail());
        verify(jwtService, times(1)).generateToken(user);
        verifyNoMoreInteractions(userRepo);
        verifyNoMoreInteractions(jwtService);
    }

    @Test
    void getAllUsers_ShouldReturnListOfUserResp() {
        List<User> userList = new ArrayList<>();
        userList.add(User.builder().id(1L).firstname("John").lastname("Doe").build());
        userList.add(User.builder().id(2L).firstname("Jane").lastname("Smith").build());

        when(userRepo.findAll()).thenReturn(userList);

        List<UserResp> userRespList = userService.getAllUsers();

        assertNotNull(userRespList);
        assertEquals(2, userRespList.size());
        assertEquals("John", userRespList.get(0).getFirstname());
        assertEquals("Doe", userRespList.get(0).getLastname());
        assertEquals("Jane", userRespList.get(1).getFirstname());
        assertEquals("Smith", userRespList.get(1).getLastname());

        verify(userRepo, times(1)).findAll();
        verifyNoMoreInteractions(userRepo);
    }

    @Test
    void testRegister_WithValidRequest_ShouldRegisterUser() throws DuplicateException {
        RegistrationReq registrationReq = new RegistrationReq();
        registrationReq.setEmail("test@example.com");
        registrationReq.setPassword("password");
        registrationReq.setFirstname("John");
        registrationReq.setLastname("Doe");
        registrationReq.setPhoneNumber("123456789");

        when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(jwtService.generateToken(any(User.class))).thenReturn("jwtToken");

        RegistrationInterimResp registrationInterimResp = userService.register(registrationReq);

        assertNotNull(registrationInterimResp);
        assertEquals("jwtToken", registrationInterimResp.getToken());
        assertEquals("John", registrationInterimResp.getUser().getFirstname());
        assertEquals("Doe", registrationInterimResp.getUser().getLastname());
        assertEquals("test@example.com", registrationInterimResp.getUser().getEmail());

        verify(userRepo, times(1)).findByEmail("test@example.com");
        verify(userRepo, times(1)).save(any(User.class));
        verify(passwordEncoder, times(1)).encode("password");
        verify(jwtService, times(1)).generateToken(any(User.class));
    }


    @Test
    void testAuthenticate_WithValidCredentials_ShouldAuthenticateUser() {
        AuthenticationReq authenticationReq = new AuthenticationReq();
        authenticationReq.setEmail("test@example.com");
        authenticationReq.setPassword("password");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(mock(Authentication.class));
        when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.of(new User()));
        when(jwtService.generateToken(any(User.class))).thenReturn("jwtToken");

        AuthenticationResp authenticationResp = userService.authenticate(authenticationReq);

        assertNotNull(authenticationResp);
        assertEquals("jwtToken", authenticationResp.getToken());

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepo, times(1)).findByEmail("test@example.com");
        verify(jwtService, times(1)).generateToken(any(User.class));
    }

    @Test
    void testAuthenticate_WithInvalidCredentials_ShouldThrowException() {
        AuthenticationReq authenticationReq = new AuthenticationReq();
        authenticationReq.setEmail("test@example.com");
        authenticationReq.setPassword("password");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, () -> userService.authenticate(authenticationReq));

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepo, never()).findByEmail(anyString());
        verify(jwtService, never()).generateToken(any(User.class));
    }

    @Test
    void testGetAllUsers_ShouldReturnListOfUsers() {
        List<User> userList = new ArrayList<>();
        userList.add(new User("John", "Doe", "test1@example.com"));
        userList.add(new User("Jane", "Smith", "test2@example.com"));

        when(userRepo.findAll()).thenReturn(userList);

        List<UserResp> userRespList = userService.getAllUsers();

        assertNotNull(userRespList);
        assertEquals(2, userRespList.size());

        verify(userRepo, times(1)).findAll();
    }
}