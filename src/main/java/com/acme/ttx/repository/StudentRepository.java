package com.acme.ttx.repository;

import com.acme.ttx.entity.ModuleType;
import com.acme.ttx.entity.Student;
import jakarta.validation.constraints.NotNull;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;
import java.util.stream.IntStream;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import static com.acme.ttx.repository.DB.STUDENTEN;
import static java.util.Collections.emptyList;
import static java.util.UUID.randomUUID;

/**
 * Mock-Klasse für den DB-Zugriff.
 */
@Repository
@Slf4j
@SuppressWarnings("PublicConstructor")
public class StudentRepository {

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
     * Abfrage, ob es einen Studenten mit gegebener E-Mail-Adresse gibt.
     *
     * @param email E-Mail-Adresse für die Suche
     * @return true, falls es einen solchen Studenten gibt.
     */
    public boolean isEmailExisting(final String email) {
        log.debug("isEmailExisting: email={}", email);
        final var result = STUDENTEN.stream()
            .filter(student -> student.getEmail().contentEquals(email))
            .count();
        log.debug("isEmailExisting: result={}", result);
        return result > 0L;
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

    /**
     * Einen Studenten anlegen.
     *
     * @param student Das Objekt des neu anzulegenden Studenten
     * @return Der neu angelegte Student mit generierter Matrikelnummer
     */
    public @NonNull Student create(final @NonNull Student student) {
        log.debug("create: {}", student);
        student.setMatrikelnummer(randomUUID());
        STUDENTEN.add(student);
        log.debug("created: {}", student);
        return student;
    }

    /**
     * Einen vorhandenen Studenten aktualisieren.
     *
     * @param student Das Obkelt mit den neuen Daten
     */
    public void update(final @NonNull Student student) {
        log.debug("update: {}", student);
        final OptionalInt index = IntStream
            .range(0, STUDENTEN.size())
            .filter(i -> Objects.equals(STUDENTEN.get(i).getMatrikelnummer(), student.getMatrikelnummer()))
            .findFirst();
        log.trace("update: index={}", student);
        if (index.isEmpty()) {
            return;
        }
        STUDENTEN.set(index.getAsInt(), student);
        log.debug("update: {}", student);
    }
}
