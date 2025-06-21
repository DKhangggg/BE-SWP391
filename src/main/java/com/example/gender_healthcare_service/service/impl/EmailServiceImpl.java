package com.example.gender_healthcare_service.service.impl;

import com.example.gender_healthcare_service.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Autowired
    private JavaMailSender MailSender;

    @Override
    public String OtpMail(String email, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        try {
            message.setFrom(fromEmail);
            message.setTo(email);
            message.setSubject("Password Reset OTP");
            message.setText("Your OTP for password reset is: " + otp +
                    "\nThis OTP is valid for 10 minutes.");
            MailSender.send(message);
            return "OTP has been sent";
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to send OTP email: " + e.getMessage();
        }
    }

    @Override
    public String resetPasswordEmail(String email, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        try {
            message.setFrom(fromEmail);
            message.setTo(email);
            message.setSubject("Password Reset Confirmation");
            message.setText("""
                    Dear User,

                    Your password has been successfully reset.

                    If you did not authorize this change, please contact our support team immediately.

                    Thanks,
                    The Gender Healthcare Service Team
                    """);
            MailSender.send(message);
            return "Password reset confirmation email has been sent";
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to send password reset confirmation email: " + e.getMessage();
        }
    }

    @Override
    public String forgotPasswordEmail(String email, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        // IMPORTANT: Replace "YOUR_FRONTEND_RESET_PASSWORD_URL" with the actual URL of your frontend page
        // This URL should ideally take the email and OTP as query parameters
        String resetPasswordUrl = "YOUR_FRONTEND_RESET_PASSWORD_URL?email=" + email + "&otp=" + otp;

        try {
            message.setFrom(fromEmail);
            message.setTo(email);
            message.setSubject("Password Reset Request");
            message.setText(String.format("""
                    Dear User,

                    You requested a password reset for your account.
                    Your One-Time Password (OTP) is: %s

                    Please click on the following link to reset your password:
                    %s

                    This OTP and link are valid for 10 minutes.
                    If you did not request a password reset, please ignore this email.

                    Thanks,
                    The Gender Healthcare Service Team
                    """, otp, resetPasswordUrl));
            MailSender.send(message);
            return "Forgot password OTP and reset link have been sent";
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to send forgot password OTP email: " + e.getMessage();
        }
    }

    @Override
    public String welcomeEmail(String email, String userName) {
        SimpleMailMessage message = new SimpleMailMessage();
        System.out.println("Attempting to send welcome email to: " + email + " for user: " + userName);
        try {
            message.setFrom(fromEmail);
            message.setTo(email);
            message.setSubject("Welcome to Gender Healthcare Service!");
            message.setText(String.format("""
                    Hello %s,

                    Welcome to Gender Healthcare Service! We are excited to have you on board.

                    Explore our services and let us know if you have any questions.

                    Best regards,
                    The Gender Healthcare Service Team
                    """, userName));
            System.out.println("Sending welcome email with details: From=" + fromEmail + ", To=" + email + ", Subject=" + message.getSubject());
            MailSender.send(message);
            System.out.println("Welcome email sent successfully to: " + email);
            return "Welcome email sent successfully";
        } catch (Exception e) {
            System.err.println("Failed to send welcome email to: " + email + ". Error: " + e.getMessage());
            e.printStackTrace(); // This will print the full stack trace to the console
            return "Failed to send welcome email: " + e.getMessage();
        }
    }
}
