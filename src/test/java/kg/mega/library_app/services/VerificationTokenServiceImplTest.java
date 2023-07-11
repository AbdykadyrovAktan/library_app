package kg.mega.library_app.services;

import kg.mega.library_app.dao.VerificationTokenRepo;
import kg.mega.library_app.models.entities.VerificationToken;
import kg.mega.library_app.services.VerificationTokenService;
import kg.mega.library_app.services.impl.VerificationTokenServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class VerificationTokenServiceImplTest {

    private final VerificationTokenRepo verificationTokenRepo = mock(VerificationTokenRepo.class);
    private final VerificationTokenService verificationTokenService = new VerificationTokenServiceImpl(verificationTokenRepo);

    @Test
    void testGetByToken() {
        String token = "token123";
        VerificationToken expectedToken = new VerificationToken(token);

        Mockito.when(verificationTokenRepo.findByToken(token)).thenReturn(expectedToken);

        VerificationToken actualToken = verificationTokenService.getByToken(token);

        assertEquals(expectedToken, actualToken);

        verify(verificationTokenRepo, times(1)).findByToken(token);
        verifyNoMoreInteractions(verificationTokenRepo);
    }
}