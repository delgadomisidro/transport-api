
package transport.transport_api.service;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import org.springframework.stereotype.Service;

import transport.transport_api.dto.LoginRequest;
import transport.transport_api.dto.LoginResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;
    private final long expiration;
    private final String issuer;
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    public AuthService(
            AuthenticationManager authenticationManager,
            JwtEncoder jwtEncoder,
            @Value("${app.jwt.expiration}") long expiration,
            @Value("${app.jwt.issuer}") String issuer) {

        this.authenticationManager = authenticationManager;
        this.jwtEncoder = jwtEncoder;
        this.expiration = expiration;
        this.issuer = issuer;
    }

    public LoginResponse login(LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()));

        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiration))
                .subject(authentication.getName())
                .build();

        JwsHeader header = JwsHeader
                .with(MacAlgorithm.HS256)
                .build();

        String token = jwtEncoder.encode(
                JwtEncoderParameters.from(header, claims)).getTokenValue();

        log.atInfo()
                .addKeyValue("event", "authentication_success")
                .addKeyValue("username", authentication.getName())
                .log("User authenticated successfully");

        return new LoginResponse(
                token,
                "Bearer",
                expiration);
    }
}
