package kg.mega.library_app.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import kg.mega.library_app.services.JwtService;
import kg.mega.library_app.services.impl.JwtServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceImplTest {
    private static final String SECRET_KEY = "6250655368566D597133743677397A244226452948404D635166546A576E5A72";
    private static final String USERNAME = "testUser";

    @Mock
    private UserDetails userDetails;

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtService = new JwtServiceImpl();
    }

    @Test
    void testExtractUsername_InvalidToken_ShouldThrowJwtException() {
        String invalidToken = "invalidToken";

        assertThrows(JwtException.class, () -> jwtService.extractUsername(invalidToken));
    }

    @Test
    void testExtractClaim_InvalidToken_ShouldThrowJwtException() {
        String invalidToken = "invalidToken";

        assertThrows(JwtException.class, () -> jwtService.extractClaim(invalidToken, Claims::getSubject));
    }

    @Test
    void testGenerateToken_WithUserDetails_ShouldReturnToken() {
        String token = jwtService.generateToken(userDetails);

        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    void testGenerateToken_WithExtraClaimsAndUserDetails_ShouldReturnTokenWithExtraClaims() {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("key1", "value1");
        extraClaims.put("key2", "value2");

        String token = jwtService.generateToken(extraClaims, userDetails);

        assertNotNull(token);
        assertTrue(token.length() > 0);
    }
}