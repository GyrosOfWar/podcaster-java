package at.wambo.podcaster.auth;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.util.Assert;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JWTFilter extends GenericFilterBean {

    private AuthenticationEntryPoint entryPoint;
    private AuthenticationManager authenticationManager;

    @Autowired
    public JWTFilter(AuthenticationManager authenticationManager, AuthenticationEntryPoint entryPoint) {
        this.authenticationManager = authenticationManager;
        this.entryPoint = entryPoint;
    }

    @Override
    public void afterPropertiesSet() throws ServletException {
        Assert.notNull(authenticationManager);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        try {
            String stringToken = req.getHeader("Authorization");
            if (stringToken == null) {
                throw new InsufficientAuthenticationException("Authorization header not found");
            }

            // remove schema from token
            String authorizationSchema = "Bearer";
            if (!stringToken.contains(authorizationSchema)) {
                throw new InsufficientAuthenticationException("Authorization schema not found");
            }
            stringToken = stringToken.substring(authorizationSchema.length()).trim();

            try {
                JWT jwt = JWTParser.parse(stringToken);
                JWTToken token = new JWTToken(jwt);

                Authentication auth = authenticationManager.authenticate(token);
                SecurityContextHolder.getContext().setAuthentication(auth);
                chain.doFilter(request, response);
            } catch (java.text.ParseException e) {
                throw new BadCredentialsException("Invalid token");
            }
        } catch (AuthenticationException e) {
            SecurityContextHolder.clearContext();
            if (entryPoint != null) {
                entryPoint.commence(req, res, e);
            }
        }
    }
}