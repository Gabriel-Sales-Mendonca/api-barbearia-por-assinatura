package com.gabrielsales.AEliteBarberShop.configs;

import com.gabrielsales.AEliteBarberShop.repositories.UserRepository;
import com.gabrielsales.AEliteBarberShop.services.TokenService;
import com.gabrielsales.AEliteBarberShop.services.VerificationSignatureTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(VerificationSignatureTokenService.class);
    private final TokenService tokenService;
    private final UserRepository userRepository;

    public SecurityFilter(TokenService tokenService, UserRepository userRepository) {
        this.tokenService = tokenService;
        this.userRepository = userRepository;
    }

    // Esse método é chamado automaticamente pelo Spring Security, quando a linha de addFilterBefore que possui uma classe que extende OncePerRequestFilter é executada
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.debug("Requisição recebida para URI: {}", request.getRequestURI());

        var token = this.recoverToken(request);
        if (token != null) {
            var subject = tokenService.validateToken(token);
            UserDetails user = userRepository.findByLogin(subject);

            // Cria o objeto de autenticação do Spring com o usuário e suas permissões
            var authentication = new UsernamePasswordAuthenticationToken(
                    user, // principal (usuário autenticado)
                    null, // credentials (null porque o token já foi validado)
                    user.getAuthorities() // roles/permissões do usuário
            );
            // Registra o usuário como autenticado no contexto de segurança do Spring
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        // Continua o processamento da requisição, passando para os próximos filtros (ou controller se os filtros tiverem acabado)
        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("token")) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
