package com.example.polls.security;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//JWTAuthenticationFilter, чтобы получить токен JWT из запроса,
// проверить его, загрузить пользователя, связанного с токеном, и передать его в Spring Security -

//Для любого входящего запроса выполняется этот класс фильтра. Он проверяет, есть ли в запросе действительный токен JWT.
// Если у него есть действительный токен JWT,
// он устанавливает аутентификацию в контексте, чтобы указать, что текущий пользователь аутентифицирован.
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {

       try {

           //мы сначала анализируем JWT, полученный из заголовка авторизации запроса
           String jwt = getJwtFromRequest(httpServletRequest);

           if(StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)){

               //получаем идентификатор пользователя
               Long userId = tokenProvider.getUserIdFromJWT(jwt);

               //После этого мы загружаем данные пользователя из базы данных
               UserDetails userDetails = customUserDetailsService.loadUserById(userId);
               //Аутентификация юзера по токену
               UsernamePasswordAuthenticationToken authenticationToken =
                       new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

               //Установка деталей запроса для данного юзера
               authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));

               //Установка данного пользователя с токеном в контекст Spring Security
               SecurityContextHolder.getContext().setAuthentication(authenticationToken);
           }
       }catch (Exception ex) {
           logger.error("Could not set user authentication in security context", ex);
       }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    //Получение токена через request
    private String getJwtFromRequest(HttpServletRequest request){
        String bearerToken = request.getHeader("Authorization");
        System.err.println("bearerToken: " + bearerToken);
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            String bearerTokenSubString = bearerToken.substring(7, bearerToken.length());
            System.err.println("bearerToken after subString: "+bearerTokenSubString);
            return bearerTokenSubString;
        }
        return  null;
    }
}
