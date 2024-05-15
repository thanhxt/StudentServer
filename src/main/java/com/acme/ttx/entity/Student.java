package com.acme.ttx.entity;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.builder.ToStringExclude;

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
     * Die ID des Studenten.
     */
    @EqualsAndHashCode.Include
    private UUID id;

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
     * Das Geburtsdatum des Studenten.
     */
    private LocalDate geburtsdatum;

    /**
     * Die Adresse des Studenten.
     */
    @ToString.Exclude
    private Adresse adresse;

     /**
      * Semester des Studenten.
      */
     private SemesterType semester;

     /**
     * Guthaben des Studenten.
     */
    @ToString.Exclude
    private List<Guthaben> guthaben;

    /**
     * Belegte Module des Studenten.
     */
    @ToStringExclude
    private List<ModuleType> module;
}
