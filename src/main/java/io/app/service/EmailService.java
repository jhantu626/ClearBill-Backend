package io.app.service;

public interface EmailService {
    public boolean sendMail(String email,String subject,String message);
}
