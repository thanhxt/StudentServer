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

    NotFoundException(final UUID id) {
        super("Kein Studenten mit der Id " + id + " gefunden");
        this.id = id;
        this.suchkriterien = null;
    }

    NotFoundException(final Map<String, List<String>> suchkriterien) {
        super("Keinen Studenten gefunden");
        id = null;
        this.suchkriterien = suchkriterien;
    }

    NotFoundException() {
        super("Keine Studenten gefunden");
        id = null;
        suchkriterien = null;
    }

}
