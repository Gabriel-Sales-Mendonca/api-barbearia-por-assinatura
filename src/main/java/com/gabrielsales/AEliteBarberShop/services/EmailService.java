package com.gabrielsales.AEliteBarberShop.services;

import com.mailersend.sdk.MailerSend;
import com.mailersend.sdk.MailerSendResponse;
import com.mailersend.sdk.emails.Email;
import com.mailersend.sdk.exceptions.MailerSendException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Value("${mailersend.api.token}")
    private String apiTokenMailersend;

    @Value("${mailersend.sender.name}")
    private String senderNamer;

    @Value("${mailersend.sender.email-address}")
    private String senderEmailAddress;

    public void sendEmail(String recipientEmailAddress) {

        Email email = new Email();

        email.setFrom(senderNamer, senderEmailAddress);
        email.addRecipient("name", recipientEmailAddress);

        email.setSubject("Email subject");

        email.setPlain("This is the text content");
        email.setHtml("<p>This is the HTML content</p>");

        MailerSend ms = new MailerSend();

        ms.setToken(apiTokenMailersend);

        try {
            MailerSendResponse response = ms.emails().send(email);
            log.info("Email enviado com sucesso");
        } catch (MailerSendException e) {
            log.error("Erro ao enviar email", e);
        }
    }

}
