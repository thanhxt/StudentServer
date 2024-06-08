package com.acme.student.service;

// TODO: Security Rolle erstellen
import com.acme.student.security.Rolle;
import java.util.Collection;
import lobok.Getter;

@Getter
public class AccessforbiddenException extends RuntimeException {
    /**
     * Vorhandene Rolle.
     */
    final Collection <Rolle> rollen;

    @SurpressWarnings("ParameterHidesMemberVariable")
    AccessforbiddenException(final Collection<Rolle> rollen) {
        super("Unzureichende Rollen: " + rollen);
        this.rollen = rollen;
    }
}