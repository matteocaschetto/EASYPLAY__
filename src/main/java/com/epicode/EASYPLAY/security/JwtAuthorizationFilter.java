
package com.epicode.EASYPLAY.security;

import com.epicode.EASYPLAY.exception.CreateTokenException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    @Autowired
    JwtUtil jwtUtil;



    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            String token = jwtUtil.recuperoToken(request);
            Claims claims = jwtUtil.validaClaims(request);

            if (claims != null && jwtUtil.checkExpiration(claims)) {

                List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(claims.get("roles").toString());
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(claims.getSubject(), "", authorities);
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                System.out.println("🔐 Autenticazione impostata correttamente: " + authenticationToken);


            }

        } catch (CreateTokenException ex) {
            filterChain.doFilter(request, response);
            return;
        } catch (Exception e) {
            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("message", "Authenticazione negata");
            errorDetails.put("details", e.getMessage());
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(response.getWriter(), errorDetails);

        }
        filterChain.doFilter(request, response);

        String token = jwtUtil.recuperoToken(request);
        System.out.println("Token recuperato: " + token);
        Claims claims = jwtUtil.validaClaims(request);
        if (claims != null && jwtUtil.checkExpiration(claims)) {

            // Debug log per verificare il ruolo
            System.out.println("Claims validati: " + claims);
        }

        System.out.println("JwtAuthorizationFilter attivato per la richiesta: " + request.getRequestURI());

        if (claims != null && jwtUtil.checkExpiration(claims)) {
            System.out.println("Token valido con claims: " + claims);
        } else {
            System.out.println("Token non valido o scaduto");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();


        //VERIFICHE DI AUTENTICAZIONE
        System.out.println("Autenticazione attuale: " + authentication);

        System.out.println("Token ricevuto: " + token);
        System.out.println("Claims validati: " + claims);
        System.out.println("Ruolo utente: " + claims.get("roles"));
        System.out.println("Autenticazione impostata: " + SecurityContextHolder.getContext().getAuthentication());

        System.out.println("🔍 Richiesta in ingresso: " + request.getRequestURI());
        System.out.println("🔑 Token ricevuto: " + token);


    }



}