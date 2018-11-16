package at.wambo.podcaster.auth;

import at.wambo.podcaster.model.User;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.SignedJWT;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Created by martin on 18.11.16.
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

  private final UserDetailsService userDetailsService;
  @Value("${jwt.secret}")
  private String secret;

  @Autowired
  public JwtFilter(UserDetailsService userDetailsService) {
    this.userDetailsService = userDetailsService;
  }

  private boolean validateToken(UserDetails userDetails, JWT token) throws ParseException {
    User user = (User) userDetails;
    Date now = new Date();

    return user != null &&
        token.getJWTClaimsSet().getSubject().equals(user.getUsername()) &&
        token.getJWTClaimsSet().getExpirationTime().after(now);
  }

  private SignedJWT parseToken(String stringToken) {
    final String authorizationSchema = "Bearer";
    if (stringToken == null) {
      logger.debug("Missing authorization header");
      return null;
    }
    if (!stringToken.contains(authorizationSchema)) {
      logger.debug("Missing authorization schema");
      return null;
    }
    stringToken = stringToken.substring(authorizationSchema.length()).trim();
    try {
      SignedJWT jwt = SignedJWT.parse(stringToken);
      JWSVerifier verifier = new MACVerifier(secret);
      if (jwt.verify(verifier)) {
        return jwt;
      } else {
        return null;
      }
    } catch (ParseException | JOSEException e) {
      logger.debug("Error parsing token", e);
      return null;
    }
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    String stringToken = request.getHeader("Authorization");
    JWT token = parseToken(stringToken);
    try {
      if (token != null) {
        String username = token.getJWTClaimsSet().getSubject();
        if (username != null) {
          UserDetails details = userDetailsService.loadUserByUsername(username);
          if (validateToken(details, token)) {
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                details, null, details.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
          }
        }
      }
    } catch (ParseException e) {
      logger.debug("Error parsing token", e);
    }

    filterChain.doFilter(request, response);
  }
}
