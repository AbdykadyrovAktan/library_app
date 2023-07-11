package kg.mega.library_app.services.impl;

import kg.mega.library_app.dao.VerificationTokenRepo;
import kg.mega.library_app.models.entities.VerificationToken;
import kg.mega.library_app.services.VerificationTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j

@Service
public class VerificationTokenServiceImpl implements VerificationTokenService {
    private final VerificationTokenRepo verificationTokenRepo;

    @Autowired
    public VerificationTokenServiceImpl(VerificationTokenRepo verificationTokenRepo) {
        this.verificationTokenRepo = verificationTokenRepo;
    }

    @Override
    public VerificationToken getByToken(String token) {
        return verificationTokenRepo.findByToken(token);
    }
}
