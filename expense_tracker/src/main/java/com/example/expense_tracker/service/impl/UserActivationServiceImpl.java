package com.example.expense_tracker.service.impl;

import com.example.expense_tracker.model.entity.UserActivationCodeEntity;
import com.example.expense_tracker.model.event.UserRegisteredEvent;
import com.example.expense_tracker.repository.UserRepository;
import com.example.expense_tracker.service.EmailService;
import com.example.expense_tracker.service.UserActivationCodeRepository;
import com.example.expense_tracker.service.UserActivationService;
import jakarta.transaction.Transactional;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Random;

@Service
public class UserActivationServiceImpl implements UserActivationService {

    private static final String ACTIVATION_CODE_SYMBOLS = "abcdefghijklmnopqrstuvwxyzABCDEFGJKLMNPRSTUVWXYZ0123456789";
    private static final int ACTIVATION_CODE_LENGTH = 20;
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final UserActivationCodeRepository userActivationCodeRepository;

    public UserActivationServiceImpl(EmailService emailService, UserRepository userRepository, UserActivationCodeRepository userActivationCodeRepository) {
        this.emailService = emailService;
        this.userRepository = userRepository;
        this.userActivationCodeRepository = userActivationCodeRepository;
    }


    @Override
    @EventListener
    public void userRegistered(UserRegisteredEvent event) {
        String activationCode = createActivationCode(event.getUserEmail());
        emailService.sendRegistrationEmail(
                event.getUserEmail(),
                event.getUserNames(),
                activationCode);
    }

    @Transactional
    @Override
    public void cleanUpObsoleteActivationLinks() {
        LocalDateTime expiredBefore = LocalDateTime.now().minusHours(24);
        userActivationCodeRepository.deleteExpiredActivationCodes(expiredBefore);

    }

    @Override
    public String createActivationCode(String userEmail) {
        String activationCode = generateActivationCode();

        UserActivationCodeEntity userActivationCodeEntity = new UserActivationCodeEntity()
                .setActivationCode(activationCode)
                .setUser(userRepository.findByEmail(userEmail).orElseThrow(()
                        -> new UsernameNotFoundException("User not found with email: " + userEmail)));

        userActivationCodeRepository.save(userActivationCodeEntity);

        return activationCode;
    }

    private static String generateActivationCode() {

        StringBuilder activationCode = new StringBuilder();
        Random random = new SecureRandom();

        for (int i = 0; i < ACTIVATION_CODE_LENGTH; i++) {
            int randomInx = random.nextInt(ACTIVATION_CODE_SYMBOLS.length()); //генерира случайно число от до 0 до дължината на Стринга
            activationCode.append(ACTIVATION_CODE_SYMBOLS.charAt(randomInx));//взима сумвола който се намира на индекса
        }

        return activationCode.toString();
    }
}
