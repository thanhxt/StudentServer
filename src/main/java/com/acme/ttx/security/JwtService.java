package com.acme.ttx.security;


import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Service-Klasse, um Benutzernamen und Rollen aus einem JWT von Keycloak zu extrahieren.
 */
@Service
@Slf4j
public class JwtService {

    /**
     * Zu einem gegebenen JWT wird der zugehörige Username gesucht.
     *
     * @param jwt JWT für Security
     * @return Der gesuchte Username oder null
     */
    public String getUsername(final Jwt jwt) {
        log.debug("getUsername");
        if (jwt == null) {
            throw new UsernameNotFoundException("JWT == null");
        }
        final var username = (String) jwt.getClaims().get("preffered_username");
        log.debug("getUsername: username={}", username);
        return username;
    }

    public List<Rolle> getRollen(final Jwt jwt) {
        @SuppressWarnings("unchecked")
        final var realmAccess = (Map<String, List<String>>) jwt.getClaims().get("realm_access");
        final var rollenStr = realmAccess.get("roles");
        log.trace("getRollen: rollenStr={}", rollenStr);
        return rollenStr
            .stream()
            .map(Rolle::of)
            .filter(Objects::nonNull)
            .toList();
    }
}
