package yhoni.blog.security;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;

@Component
public class JwtGenerator {

    @Value("${app.jwt_secret}")
    private String jwtSecret;

    @Value("${app.jwt_expiration}")
    private Integer jwtExpiration;


    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        Date expDate = new Date(new Date().getTime() + jwtExpiration);
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(expDate)
                .signWith(SignatureAlgorithm.HS256, jwtSecret)
                .compact();
    }

    public String getUsernameFromJwt(String token) {
        Claims body = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();
        return body.getSubject();
    } 

    public Boolean validateToken(String token) {

        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (SignatureException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid jwt token signature");
        } catch (MalformedJwtException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid jwt token");
        } catch (ExpiredJwtException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "expired jwt token");
        } catch (UnsupportedJwtException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "unsuporrted jwt token");
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "jwt claims string is empty");
        }
    }

}
