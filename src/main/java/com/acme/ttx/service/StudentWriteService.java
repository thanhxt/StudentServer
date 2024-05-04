package com.acme.ttx.service;

import com.acme.ttx.entity.Student;
import com.acme.ttx.repository.StudentRepository;
import jakarta.validation.Valid;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * Anwendungslogik für Kunden auch mit Bean Validation.
 */
@Service
@Validated
@RequiredArgsConstructor
@Slf4j
public class StudentWriteService {
    private final StudentRepository repo;

    /**
     * Einen neuen Studenten anlegen.
     *
     * @param student Das Objekt das neu anzulegenden Studenten.
     * @return Der neu angelegte Student mit generierter Matrikelnummer
     * @throws EmailExistsException Es gibt bereits einen Studenten mit der E-Mail-Adresse
     */
    public Student create(@Valid final Student student) {
        log.debug("Student: {}", student);

        if (repo.isEmailExisting(student.getEmail())) {
            throw new EmailExistsException(student.getEmail());
        }

        final var studentDB = repo.create(student);
        log.debug("create: {}", studentDB);
        return studentDB;
    }

    /**
     * Einen vorhandenen Studenten aktualisieren.
     *
     * @param student das Objekt mit den neuen Daten (ohne Matrikelnummer)
     * @param matrikelnummer Matrikelnummer des zu aktualisierenden Studenten
     * @throws NotFoundException Kein Student zur Matrikelnummer vorhanden.
     * @throws EmailExistsException Es gibt bereits einen Studenten mit der E-Mail-Adresse.
     */
    public void update(@Valid final Student student, final UUID matrikelnummer) {
        log.debug("update: {}", student);
        log.debug("update: matrikelnummer:{}", matrikelnummer);

        final var email = student.getEmail();
        final var studentDB = repo
            .findByMatrikelnummer(matrikelnummer)
            .orElseThrow(() -> new NotFoundException(matrikelnummer));
        if (!Objects.equals(email, studentDB.getEmail()) && repo.isEmailExisting(email)) {
            log.debug("update: email {} existiert", email);
            throw new EmailExistsException(email);
        }

        student.setMatrikelnummer(matrikelnummer);
        repo.update(student);
    }
}
