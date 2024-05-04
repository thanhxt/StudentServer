package com.acme.ttx.rest;

import com.acme.ttx.entity.ModuleType;
import com.acme.ttx.entity.SemesterType;
import java.time.LocalDate;
import java.util.List;

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
    String nachname,
    String name,
    String email,
    LocalDate geburtsdatum,
    AdresseDTO adresse,
    List<GuthabenDTO> guthaben,
    SemesterType semester,
    List<ModuleType> module
) {
}
