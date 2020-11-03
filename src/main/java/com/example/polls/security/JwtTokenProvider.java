package com.example.polls.security;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component // помечает класс в качестве кандидата для создания Spring бина.
// Следующий служебный класс будет использоваться для создания JWT после успешного входа пользователя в систему
// и проверки JWT, отправленного в заголовке авторизации запросов:

public class JwtTokenProvider {
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${app.jwtSecret}")
    private String jwtSecret;

    @Value("${app.jwtExpirationInMs}")
    private int jwtExpirationInMs;

    public String generateToken(Authentication authentication) {
        //Authentication Представляет токен для запроса аутентификации или аутентифицированного участника

            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            System.err.println("UserPrincipalPolls :" + userPrincipal);

            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

            return Jwts.builder()
                    .setSubject(Long.toString(userPrincipal.getId()))
                    .setIssuedAt(new Date())
                    .setExpiration(expiryDate)
                    //SignatureAlgorithm нужно учитывать алгоритм подписи при создании токена
                    //TODO прочитать про SignatureAlgorithm
                    .signWith(SignatureAlgorithm.HS512,jwtSecret)
                    .compact();
    }

    public Long getUserIdFromJWT(String token){
        //Claims это фрагменты информации, каждая из которых представлена ​​в виде пары ключ-значение в формате JSON.

        //Приведенный ниже JSON представляет собой пример набора требований.
        /*
           {
           "iss":"joe",
            "exp":1300819380,
           "http://example.com/is_root":true
            }
        */
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();

        System.err.println("check getSubjectFunc: " + claims.getSubject());
        return Long.parseLong(claims.getSubject());
    }

    //Метод для подтверждения токена
    public boolean validateToken(String authToken){
        try{
            //Синтаксический анализатор для чтения строк JWT, используемый для их преобразования в объект

            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            System.err.println("validate token: Jwts.parser() - " +Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken));
            return true;
        } catch (SignatureException ex){
            logger.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            logger.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            logger.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty.");
        }
        return false;
    }

}
