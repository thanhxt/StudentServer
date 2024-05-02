package com.acme.ttx.repository;

import com.acme.ttx.entity.Adresse;
import com.acme.ttx.entity.Guthaben;
import com.acme.ttx.entity.ModuleType;
import com.acme.ttx.entity.SemesterType;
import com.acme.ttx.entity.Student;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Currency;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import static com.acme.ttx.entity.ModuleType.MATHEMATIK_1;
import static com.acme.ttx.entity.ModuleType.MATHEMATIK_2;
import static com.acme.ttx.entity.ModuleType.SOFTWAREARCHITEKTUR;
import static com.acme.ttx.entity.ModuleType.SOFTWAREENGINEERING;
import static java.util.Collections.emptyList;

/**
 * Mock-Klasse für den DB-Zugriff.
 */
@Repository
@Slf4j
public class StudentRepository {
    private static final int JAHR = 2000;
    private static final int MONAT = 7;
    private static final int TAG = 12;
    private static final double BETRAG = 10.0;
    private static final List<Student> STUDENTEN = Stream.of(
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
                .plz("86445")
                .build())
            .geburtsdatum(LocalDate.of(JAHR, MONAT, TAG))
            .guthaben(List.of(
                Guthaben.builder()
                    .betrag(BigDecimal.valueOf(BETRAG))
                    .waehrung(Currency.getInstance("EUR"))
                    .build())
            )
            .semester(SemesterType.SEMESTER_3)
            .module(List.of(MATHEMATIK_1, SOFTWAREARCHITEKTUR))
            .build()
    ).toList();

    /**
     * Alle Studenten als Collection ermitteln, wie sie später auch in der DB vorkommen.
     *
     * @return Alle Studenten
     */
    public Collection<Student> findAll() {
        return STUDENTEN;
    }

    /**
     * Student zu einer gegebenen Matrikelnummer in der DB ermitteln.
     *
     * @param matrikelnummer Matrikelnummer für die Suche
     * @return Gefundener Student oder leeres Optional
     */
    public Optional<Student> findByMatrikelnummer(final UUID matrikelnummer) {
        log.debug("findByMatrikelnummer: id={}", matrikelnummer);
        final var result = STUDENTEN.stream()
            .filter(student -> Objects.equals(student.getMatrikelnummer(), matrikelnummer))
            .findFirst();
        log.debug("findByMatrikelnummer: {}", result);
        return result;
    }

    /**
     * Studenten anhand des Nachnamen suchen.
     *
     * @param nachname Der Nachname des gesuchen Studenten
     * @return Die gefundenen Studenten oder eine leere Collection.
     */
    public Collection<Student> findByNachname(final @NonNull String nachname) {
        log.debug("findByNachname: nachname={}", nachname);
        final var result = STUDENTEN.stream()
            .filter(student -> Objects.equals(student.getNachname(), nachname))
            .toList();
        log.debug("findByNachname: student={}", result);
        return result;
    }

    /**
     * Studenten anhand des Namen suchen.
     *
     * @param name Der Name des gesuchten Studenten
     * @return Die gefundenen Studenten oder eine Leere Collection
     */
    public Collection<Student> findByName(final String name) {
        log.debug("findByName: name: {}", name);
        final var result = STUDENTEN.stream()
            .filter(student -> Objects.equals(student.getName(), name))
            .toList();
        log.debug("findByName: student={}", result);
        return result;
    }

    /**
     * Studenten zu gegebener Emailadresse aus der DB ermitteln.
     *
     * @param email Emailadresse für die Suche
     * @return Gefundener Student oder leeres Optional
     */
    public Optional<Student> findByEmail(final String email) {
        log.debug("findByEmail: email: {}", email);
        final var result = STUDENTEN.stream()
            .filter(student -> student.getEmail().contentEquals(email))
            .findFirst();
        log.debug("findByEmail: {}", result);
        return result;
    }

    /**
     * Studenten zu gegebenen Semester aus der DB ermitteln.
     *
     * @param semesterStr Semester als String
     * @return Gefundene Studenten oder leere Collection
     */
    public Collection<Student> findBySemester(final String semesterStr) {
        log.debug("findBySemester: semesterStr: {}", semesterStr);
        final var result = STUDENTEN.stream()
            .filter(student -> Objects.equals(student.getSemester().getValue(), semesterStr))
            .toList();
        log.debug("findBySemester: {}", result);
        return result;
    }

    /**
     * Studenten zu gegebenen Modulen aus der DB ermitteln.
     *
     * @param moduleStr Modul für die Suche
     * @return Gefundene Studenten oder leere Collection
     */
    private @NotNull Collection<Student> findByModule(final Collection<String> moduleStr) {
        log.debug("findByModule: moduleStr: {}", moduleStr);
        final var module = moduleStr.stream()
            .map(ModuleType::of)
            .toList();
        if (module.contains(null)) {
            return emptyList();
        }

        log.trace("findByModule: module={}", module);
        final var studenten = STUDENTEN.stream()
            .filter(student -> {
                final Collection<ModuleType> studentModule = new HashSet<>(student.getModule());
                return studentModule.containsAll(module);
            })
            .toList();
        log.debug("findByModule: student={}", studenten);
        return studenten;
    }

    /**
     * Kunden anhand von Suchkriterien ermitteln. Z.B. mit GET
     * <a href="https://localhost:8080/api?modul=SWA&modul=MA1">...</a>
     *
     * @param suchkriterien Suchkriterien.
     * @return Gefundene Studenten oder leere Collection.
     */
    @SuppressWarnings("ReturnCount")
    public @NotNull Collection<Student> find(final Map<String, ? extends  List<String>> suchkriterien) {
        log.debug("find: suchkriterien={}", suchkriterien);

        if (suchkriterien.isEmpty()) {
            return findAll();
        }

        for (final var entry : suchkriterien.entrySet()) {
            switch (entry.getKey()) {
                case "email" -> {
                    final var student = findByEmail(entry.getValue().getFirst()).orElse(null);
                    return student == null ? emptyList() : List.of(student);
                }
                case "modul" -> {
                    return findByModule(entry.getValue());
                }
                case "name" -> {
                    return findByName(entry.getValue().getFirst());
                }
                case "nachname" -> {
                    return findByNachname(entry.getValue().getFirst());
                }
                case "semester" -> {
                    return findBySemester(entry.getValue().getFirst());
                }
                default -> {
                    log.debug("find: ungueltiges suchkriterien={}", entry.getKey());
                    return emptyList();
                }
            }
        }

        return emptyList();
    }
}
