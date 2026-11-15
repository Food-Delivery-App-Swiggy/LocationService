package com.swiggy.LocationService.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;

@Configuration
public class JwtSecurityConfig {

    @Bean
    public JwtDecoder jwtDecoder(
            @Value("${security.jwt.secret}") String secret) {

        byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);

        if (bytes.length < 32) {
            throw new IllegalArgumentException(
                    "JWT secret must be at least 32 bytes");
        }

        SecretKey key = new SecretKeySpec(bytes, "HmacSHA256");

        return NimbusJwtDecoder.withSecretKey(key)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }

    @Bean
    public Converter<Jwt,AbstractAuthenticationToken> jwtAuthenticationConverter() {

        return new Converter<Jwt, AbstractAuthenticationToken>() {
            @Override
            public AbstractAuthenticationToken convert(Jwt jwt) {

                List<String> roles = jwt.getClaimAsStringList("roles");

                Collection<GrantedAuthority> authorities =
                        roles == null
                                ? List.of()
                                : roles.stream()
                                .map(role -> new SimpleGrantedAuthority(
                                        role.startsWith("ROLE_")
                                                ? role
                                                : "ROLE_" + role))
                                .map(GrantedAuthority.class::cast)
                                .toList();

                return new JwtAuthenticationToken(
                        jwt, authorities, jwt.getSubject());
            }
        };
    }


    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http, Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers("/api/v1/auth/login").permitAll()
                        .requestMatchers("/api/v1/auth/register").permitAll()
                        .requestMatchers("/internal/users/**").permitAll()
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth -> oauth
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(
                                jwtAuthenticationConverter)));

        return http.build();
    }
}
