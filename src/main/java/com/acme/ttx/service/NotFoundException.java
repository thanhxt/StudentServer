package com.acme.ttx.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;

/**
 * RuntimeException, falls kein Student gefunden wurde.
 */
@Getter
public final class NotFoundException extends RuntimeException {
    /**
     * Nicht-vorhandene ID.
     */
    private final UUID id;

    /**
     * Suchkriterien zu denen nichts gefudnen wurde.
     */
    private final Map<String, List<String>> suchkriterien;

    NotFoundException(final UUID matrikelnummer) {
        super("Kein Studenten mit der Matrikelnummer " + matrikelnummer + " gefunden");
        this.id = matrikelnummer;
        this.suchkriterien = null;
    }

    NotFoundException(final Map<String, List<String>> suchkriterien) {
        super("Keinen Studenten gefunden");
        id = null;
        this.suchkriterien = suchkriterien;
    }

}
