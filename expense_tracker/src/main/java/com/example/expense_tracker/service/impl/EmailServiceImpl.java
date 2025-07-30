package com.example.expense_tracker.service.impl;

import com.example.expense_tracker.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;
    private final String expenseTrackerEmail;

    public EmailServiceImpl(JavaMailSender javaMailSender, @Value("${mail.expenseTracker}") String expenseTracker) {
        this.javaMailSender = javaMailSender;
        this.expenseTrackerEmail = expenseTracker;
    }

    @Override
    public void sendRegistrationEmail(String userEmail, String userName, String activationCode) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

        try {
            mimeMessageHelper.setTo(userEmail);
            mimeMessageHelper.setFrom(expenseTrackerEmail);
            mimeMessageHelper.setReplyTo(expenseTrackerEmail);
            mimeMessageHelper.setSubject("Welcome to Expense Tracker!");
            mimeMessageHelper.setText(
                "Welcome to Expense Tracker!\n" +
                "Hello " + userName + ",\n" +
                "Your registration was successful!\n\n" +
                "Best regards,\nThe Expense Tracker Team", 
                false); // false = plain text, не HTML
            
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send registration email", e);
        }


    }
}
