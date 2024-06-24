package com.acme.ttx.service;

import com.acme.ttx.security.Rolle;
import lombok.Getter;

import java.util.Collection;

@Getter
public class AccessForbiddenException extends RuntimeException {
    /**
     * Vorhandene Rolle.
     */
    private final Collection<Rolle> rollen;

    @SuppressWarnings("ParameterHidesMemberVariable")
    AccessForbiddenException(final Collection<Rolle> rollen) {
        super("Unzureichende Rollen: " + rollen);
        this.rollen = rollen;
    }
}
