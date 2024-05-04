package com.acme.ttx.repository;

import com.acme.ttx.entity.Adresse;
import com.acme.ttx.entity.Guthaben;
import com.acme.ttx.entity.SemesterType;
import com.acme.ttx.entity.Student;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static com.acme.ttx.entity.ModuleType.MATHEMATIK_1;
import static com.acme.ttx.entity.ModuleType.MATHEMATIK_2;
import static com.acme.ttx.entity.ModuleType.SOFTWAREARCHITEKTUR;
import static com.acme.ttx.entity.ModuleType.SOFTWAREENGINEERING;

/**
 * Emulation der Datenbasis für persistente Studenten.
 */
@SuppressWarnings({ "UtilityClassCanBeEnum", "UtilityClass", "MagicNumber", "RedundantSuppression", "java:S1192"})
final class DB {
    /**
     * Liste der Studenten zur Emulation der DB.
     */
    @SuppressWarnings("StaticCollection")
    static final List<Student> STUDENTEN;
    static final int JAHR = 2000;
    static final int MONAT = 7;
    static final int TAG = 12;
    static final double BETRAG = 10.0;

    static {
        STUDENTEN = Stream.of(
            Student.builder()
                .matrikelnummer(UUID.fromString("00000000-0000-0000-0000-000000000001"))
                .name("Max")
                .nachname("Hahn")
                .adresse(Adresse.builder()
                    .strasse("Theodore straße")
                    .ort("Mannheim")
                    .plz("67061")
                    .build())
                .geburtsdatum(LocalDate.of(JAHR, MONAT, TAG))
                .email("MaxHahn@gmail.com")
                .guthaben(List.of(
                    Guthaben.builder()
                        .betrag(BigDecimal.valueOf(BETRAG))
                        .waehrung(Currency.getInstance("EUR"))
                        .build())
                )
                .semester(SemesterType.SEMESTER_3)
                .module(List.of(SOFTWAREENGINEERING, MATHEMATIK_2))
                .build(),
            Student.builder()
                .matrikelnummer(UUID.fromString("00000000-0000-0000-0000-000000000002"))
                .name("Thomas")
                .nachname("Müller")
                .adresse(Adresse.builder()
                    .strasse("Leibnizstraße")
                    .ort("München")
                    .plz("86443")
                    .build())
                .geburtsdatum(LocalDate.of(JAHR, MONAT, TAG))
                .email("thomas.mueller@web.de")
                .guthaben(List.of(
                    Guthaben.builder()
                        .betrag(BigDecimal.valueOf(BETRAG))
                        .waehrung(Currency.getInstance("EUR"))
                        .build())
                )
                .semester(SemesterType.SEMESTER_3)
                .module(List.of(MATHEMATIK_1, SOFTWAREARCHITEKTUR))
                .build()

        )
        // CAVEAT Stream.toList() erstellt eine "immutable" List
        .collect(Collectors.toList());
    }

    private DB() {

    }
}
