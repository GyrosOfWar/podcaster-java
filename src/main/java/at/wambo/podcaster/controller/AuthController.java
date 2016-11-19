package at.wambo.podcaster.controller;

import at.wambo.podcaster.auth.JwtResponse;
import at.wambo.podcaster.forms.CreateUserRequest;
import at.wambo.podcaster.model.LoginRequest;
import at.wambo.podcaster.model.User;
import at.wambo.podcaster.repository.UserRepository;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

import static at.wambo.podcaster.controller.UserController.PASSWORD_ENCODER;

/**
 * Created by martin on 19.11.16.
 */
@RestController
public class AuthController {
    public static final int JWT_DURATION = 12 * 60 * 60 * 1000;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    @Value("${jwt.secret}")
    private String secret;

    @Autowired
    public AuthController(UserRepository userRepository, AuthenticationManager authenticationManager, UserDetailsService userDetailsService) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
    }

    private String generateToken(UserDetails userDetails, byte[] secret) throws JOSEException {
        JWSSigner signer = new MACSigner(secret);

        Date now = new Date();
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(userDetails.getUsername())
                .issueTime(now)
                .expirationTime(new Date(now.getTime() + JWT_DURATION))
                .issuer("podcaster")
                .build();
        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
        signedJWT.sign(signer);
        return signedJWT.serialize();
    }

    private String validateRequest(CreateUserRequest request) {
        if (request.hasNullValues()) {
            return "Request has null values.";
        }

        if (!request.getPassword().equals(request.getPasswordRepeated())) {
            return "Passwords do not match!";
        }
        if (userRepository.findByName(request.getUsername()).isPresent()) {
            return "User with this name already exists.";
        }
        return null;
    }

    private User create(CreateUserRequest request) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setName(request.getUsername());
        user.setPwHash(PASSWORD_ENCODER.encode(request.getPassword()));
        return this.userRepository.save(user);
    }

    @RequestMapping(path = "/auth/register", method = RequestMethod.POST)
    public ResponseEntity<?> register(@RequestBody CreateUserRequest request) {
        String error = validateRequest(request);
        if (error == null) {
            return ResponseEntity.ok(create(request));
        } else {
            return ResponseEntity.badRequest().body(error);
        }
    }


    @RequestMapping(path = "/auth/token", method = RequestMethod.POST)
    public JwtResponse getToken(@RequestBody LoginRequest loginRequest) throws JOSEException {
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Reload password post-security so we can generate token
        final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());
        final String token = generateToken(userDetails, secret.getBytes());

        return new JwtResponse(token);
    }
}