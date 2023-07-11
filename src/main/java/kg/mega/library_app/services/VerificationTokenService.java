package kg.mega.library_app.services;

import kg.mega.library_app.models.entities.VerificationToken;

public interface VerificationTokenService {
    VerificationToken getByToken(String token);
}
