package com.acme.ttx.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

import static com.acme.ttx.entity.Adresse.PLZ_PATTERN;

/**
 * ValueObject für das Neuanlegen und Ändern eines neuen Studenten.
 *
 * @param plz Postleitzahl
 * @param ort Ort
 * @param strasse Strasse
 */
@Builder
public record AdresseDTO(
    @NotNull
    @Pattern(regexp = PLZ_PATTERN)
    String plz,
    @NotBlank
    String ort,
    @NotBlank
    String strasse) {
}
