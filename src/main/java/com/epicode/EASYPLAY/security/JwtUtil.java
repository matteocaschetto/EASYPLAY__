

package com.epicode.EASYPLAY.security;

import com.epicode.EASYPLAY.exception.CreateTokenException;
import com.epicode.EASYPLAY.model.Utente;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String JWTSECRET;
    private long scadenza = 15;
    private final String TOKEN_HEADER = "Authorization";
    private final String TOKEN_PREFIX = "Bearer ";
    private JwtParser JWTPARSER;

    @PostConstruct
    public void init() {

        JWTPARSER = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(JWTSECRET.getBytes(StandardCharsets.UTF_8)))
                .build();


    }

    public String creaToken(Utente utente) {
        Claims claims = Jwts.claims().setSubject(utente.getUsername());
        claims.put("roles", utente.getRuolo()); // Aggiungi il prefisso "ROLE_"
        claims.put("firstname", utente.getNome());
        claims.put("lastname", utente.getCognome());
        Date dataCreazioneToken = new Date();
        Date dataScadenza = new Date(dataCreazioneToken.getTime() + TimeUnit.MINUTES.toMillis(scadenza));

        String token = Jwts.builder()
                .setClaims(claims)
                .setExpiration(dataScadenza)
                /*.signWith(SignatureAlgorithm.HS256, JWTSECRET)*/
                .signWith(Keys.hmacShaKeyFor(JWTSECRET.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();

        return token;
    }

    public String recuperoToken(HttpServletRequest request) throws CreateTokenException {
        String bearerToken = request.getHeader(TOKEN_HEADER);
        if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(TOKEN_PREFIX.length());
        }
        throw new CreateTokenException("Non è stato possibile recuperare il TOKEN");
    }

    public Claims validaClaims(HttpServletRequest request) throws CreateTokenException {
        try {
            String token = recuperoToken(request);
            return JWTPARSER.parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException ex) {
            request.setAttribute("expired", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            request.setAttribute("token invalido", ex.getMessage());
            throw ex;
        }
    }

    public boolean checkExpiration(Claims claims) {
        try {
            return claims.getExpiration().after(new Date());
        } catch (Exception ex) {
            throw ex;
        }
    }
}

