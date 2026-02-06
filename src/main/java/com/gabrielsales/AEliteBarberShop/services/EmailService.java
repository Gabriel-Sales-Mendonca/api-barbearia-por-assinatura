package com.gabrielsales.AEliteBarberShop.services;

import com.mailersend.sdk.MailerSend;
import com.mailersend.sdk.MailerSendResponse;
import com.mailersend.sdk.emails.Email;
import com.mailersend.sdk.exceptions.MailerSendException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Value("${mailersend.api.token}")
    private String apiTokenMailersend;

    @Value("${mailersend.sender.name}")
    private String senderName;

    @Value("${mailersend.sender.email-address}")
    private String senderEmailAddress;

    private void sendEmail(String recipientEmail, String recipientName, String subject, String htmlContent, String plainText) {
        Email email = new Email();

        email.setFrom(senderName, senderEmailAddress);
        email.addRecipient(recipientName, recipientEmail);

        email.setSubject(subject);

        email.setPlain(plainText);
        email.setHtml(htmlContent);

        MailerSend ms = new MailerSend();

        ms.setToken(apiTokenMailersend);

        try {
            MailerSendResponse response = ms.emails().send(email);
            log.info("Email enviado com sucesso");
        } catch (MailerSendException e) {
            log.error("Erro ao enviar email para: {}", recipientEmail, e);
            throw new RuntimeException("Falha ao enviar email de verificação", e);
        }
    }

    public void sendVerificationCodeEmail(String recipientEmail, String recipientName, String code) {

        String htmlContent = loadEmailTemplate("email-verification-code.html", recipientName, code);

        String plainText = String.format(
                "Bem-vindo à A Elite Barber Shop!\n\n" +
                        "Seu código de verificação é: %s\n\n" +
                        "Este código expira em 15 minutos.\n\n" +
                        "Se você não solicitou este código, ignore este email.\n\n" +
                        "A Elite Barber Shop",
                code
        );

        String subject = "Seu código de verificação - A Elite Barber Shop";

        this.sendEmail(recipientEmail, recipientName, subject, htmlContent, plainText);
        log.info("Email com código enviado para: {}", recipientEmail);
    }

    private String loadEmailTemplate(String templateName, String userName, String verificationCode) {
        try {
            ClassPathResource resource = new ClassPathResource("templates/" + templateName);
            Path path = resource.getFile().toPath();
            String template = Files.readString(path, StandardCharsets.UTF_8);

            template = template.replace("{{USER_NAME}}", userName);
            template = template.replace("{{VERIFICATION_CODE}}", verificationCode);

            log.debug("Template de email carregado: {}", templateName);
            return template;

        } catch (IOException e) {
            log.error("Erro ao carregar template de email: {}", templateName, e);
            return getFallbackEmailTemplate(userName, verificationCode);
        }
    }

    private String getFallbackEmailTemplate(String userName, String code) {
        return "<!DOCTYPE html>" +
                "<html lang='pt-BR'>" +
                "<head><meta charset='UTF-8'></head>" +
                "<body style='font-family: Arial, sans-serif; padding: 20px; background-color: #f4f4f4;'>" +
                "  <div style='max-width: 600px; margin: 0 auto; background: white; padding: 30px; border-radius: 8px;'>" +
                "    <h1 style='color: #1a1a1a; text-align: center;'>A Elite Barber Shop</h1>" +
                "    <h2 style='color: #333;'>Olá, " + userName + "!</h2>" +
                "    <p style='color: #666; font-size: 16px;'>Seu código de verificação é:</p>" +
                "    <div style='background: #f9f9f9; border: 2px dashed #1a1a1a; padding: 20px; text-align: center; margin: 20px 0;'>" +
                "      <div style='font-size: 42px; font-weight: bold; color: #1a1a1a; letter-spacing: 8px; font-family: monospace;'>" + code + "</div>" +
                "    </div>" +
                "    <p style='color: #856404; background: #fff3cd; padding: 15px; border-radius: 4px;'>" +
                "      ⏰ <strong>Este código expira em 15 minutos</strong>" +
                "    </p>" +
                "    <p style='color: #999; font-size: 13px; margin-top: 30px;'>" +
                "      Não solicitou este código? Ignore este email." +
                "    </p>" +
                "  </div>" +
                "</body>" +
                "</html>";
    }

    public void sendAccessRecoveryEmail(String recipientEmail, String recipientName, String code) {
        String subject = "Recuperação de Senha - A Elite Barber Shop";
        String plainText = "Para recuperar sua senha acesse o Link e informe este código: " + code + ", e insira sua nova senha";

        this.sendEmail(recipientEmail, recipientName, subject, null, plainText);
    }
}
