package com.acme.ttx.rest;

/**
 * ValueObject für das Neuanlegen und Ändern eines neuen Studenten.
 *
 * @param plz Postleitzahl
 * @param ort Ort
 * @param strasse Strasse
 */
public record AdresseDTO(
    String plz,
    String ort,
    String strasse) {
}
