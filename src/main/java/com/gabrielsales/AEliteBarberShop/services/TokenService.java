package com.gabrielsales.AEliteBarberShop.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.gabrielsales.AEliteBarberShop.entities.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    public String generateToken(User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);

            String token = JWT.create()
                    .withIssuer("a-elite-barber-shop-api") // identificador da sua API (útil para validar depois)
                    .withSubject(user.getUsername()) // quem é o dono do token (identificador único do usuário)
                    .withExpiresAt(generateExpirationDate()) // tempo de expiração do token
                    .sign(algorithm); // assinatura final com a chave secreta

            return token;
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro ao gerar o token de autenticação: ", exception);
        }
    }

    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);

            return JWT.require(algorithm)
                    .withIssuer("a-elite-barber-shop-api") // deve ser igual ao que foi usado na geração
                    .build()
                    .verify(token) // verifica validade e assinatura
                    .getSubject(); // retorna o "subject" (login do usuário)
        } catch (JWTVerificationException exception) {
            // Se o token for inválido ou expirado, retorna string vazia (evita exceções no filtro)
            return "";
        }
    }

    private Instant generateExpirationDate() {
        return LocalDateTime.now()
                .plusDays(7)
                .toInstant(ZoneOffset.of("-03:00"));
    }
}
