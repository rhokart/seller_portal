package com.rhokart.sellerportal.service;


import com.rhokart.sellerportal.domain.EmailDetails;
import org.springframework.stereotype.Service;

@Service
public interface IEmailService {
    boolean sendEmailToUser(EmailDetails details);
}
