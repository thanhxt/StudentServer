package com.acme.ttx.controller;

import com.acme.ttx.entity.ModuleType;
import com.acme.ttx.entity.SemesterType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.List;
import org.hibernate.validator.constraints.UniqueElements;

/**
 * ValueObject für das Neuanlegen und Ändern eines neuen Studenten. Beim Lesen wird die Klasse
 * StudentModel für die Ausgabe verwendet.
 *
 * @param nachname Nachname eines Studenten
 * @param name Name eines Studenten
 * @param email E-Mail eines Studenten
 * @param geburtsdatum Geburtsdatum eines Studenten
 * @param adresse Adresse eines Studenten
 * @param guthaben Guthaben eines Studenten
 * @param semester Semester eines Studenten
 * @param module Belegte Module eines Studenten
 */
public record StudentDTO(
    @NotNull
    @Pattern(regexp = NACHNAME_PATTERN)
    String nachname,
    @NotNull
    @Pattern(regexp = VORNAME_PATTERN)
    String name,
    @Email
    String email,
    @Past
    LocalDate geburtsdatum,
    @Valid
    @NotNull
    AdresseDTO adresse,
    List<GuthabenDTO> guthaben,
    SemesterType semester,
    @UniqueElements
    List<ModuleType> module
) {
    /**
     * Muster für einen gültigen Nachnamen.
     */
    public static final String NACHNAME_PATTERN =
        "(o'|von|von der|von und zu|van)?[A-ZÄÖÜ][a-zäöüß]+(-[A-ZÄÖÜ][a-zäöüß]+)?";

    /**
     * Muster für einen gültigen Vornmen.
     */
    public static final String VORNAME_PATTERN =
        "[A-ZÄÖÜ][a-zäöüß]+(-[A-ZÄÖÜ][a-zäöüß]+)?";

    /**
     * Marker-Interface für Jakarta Validations: zusätzliche Validierung beim Neuanlegen.
     */
    interface OnCreate { }
}
