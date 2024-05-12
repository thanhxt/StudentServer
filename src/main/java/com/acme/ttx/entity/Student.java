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
 * <img src="../../../../../../../extras/doc/Student.png" alt="Klassendiagramm">
 *
 */
@Builder
@Getter
@Setter
@ToString
public class Student {
    /**
     * Die Matrikelnummer des Studenten.
     */
    @EqualsAndHashCode.Include
    private UUID matrikelnummer;

    /**
     * Nachname des Studenten.
     */
    private String nachname;

    /**
     * Vorname des Studenten.
     */
    private String name;

    /**
     * Email des Studenten.
     */
    private String email;

    /**
     * Das Geburtsdatum des Kunden.
     */
    private LocalDate geburtsdatum;

    /**
     * Die Adresse des Kunden.
     */
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
    @ToStringExclude
    private List<ModuleType> module;
}
