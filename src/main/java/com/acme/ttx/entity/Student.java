package com.acme.ttx.entity;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.builder.ToStringExclude;
import org.hibernate.validator.constraints.UniqueElements;

/**
 * Daten eines Studenten.
 */
@Builder
@Getter
@Setter
@ToString
public class Student {
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
     * Die Matrikelnummer des Studenten.
     */
    @EqualsAndHashCode.Include
    private UUID matrikelnummer;

    /**
     * Nachname des Studenten.
     */
    @NotNull
    @Pattern(regexp = NACHNAME_PATTERN)
    private String nachname;

    /**
     * Vorname des Studenten.
     */
    @NotNull
    @Pattern(regexp = VORNAME_PATTERN)
    private String name;

    /**
     * Email des Studenten.
     */
    @Email
    private String email;

    /**
     * Das Geburtsdatum des Kunden.
     */
    @Past
    private LocalDate geburtsdatum;

    /**
     * Die Adresse des Kunden.
     */
    @Valid
    @NotNull
    @ToString.Exclude
    private Adresse adresse;

    /**
     * Guthaben des Studenten.
     */
    @ToString.Exclude
    private List<Guthaben> guthaben;


    /**
     * Semester des Studenten.
     */
    private SemesterType semester;

    /**
     * Belegte Module des Studenten.
     */
    @UniqueElements
    @ToStringExclude
    private List<ModuleType> module;
}
