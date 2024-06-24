package com.acme.ttx.service;

import com.acme.ttx.entity.Student;
import com.acme.ttx.mail.Mailer;
import com.acme.ttx.repository.StudentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class StudentWriteService {
    private final StudentRepository repo;
    private final Mailer mailer;

    /**
     * Einen neuen Studenten anlegen.
     *
     * @param student Das Objekt des neu anzulegenden Studenten
     * @return Der neu angelegte Student mit generierter ID
     * @throws EmailExistsException Es gibt bereits einen Studenten mit der E-Mail-Adresse.
     */
    @Transactional
    @SuppressWarnings("TrailingComment")
    public Student create (final Student student) {
        log.debug("create: student={}", student);
        log.debug("create: adresse={}", student.getAdresse());
        log.debug("create: guthaben={}", student.getGuthaben());

        if (repo.existsById(student.getId())) {
            throw new EmailExistsException(student.getEmail());
        }


        //TODO Neuen Benutzer im IAM-System anlegen
        student.setUsername("user");

        final var studentDB = repo.save(student);

        log.trace("create: Thread-ID={}", Thread.currentThread().threadId());
        mailer.send(studentDB);

        log.debug("create: studentDB={}", studentDB);
        return studentDB;
    }

    /**
     * Einen vorhandenen Student aktualisieren.
     *
     * @param student Das Objekt mit den neuen Daten (ohne ID)
     * @param id ID des zu aktualisierenden Studenten
     * @param version Die erforderliche Version
     * @return Aktualisierter Student mit erhöhter Versionsnummer
     * @throws NotFoundException Kein Student zur ID vorhanden
     * @throws VersionOutdatedException Die Versionsnummer ist veraltet und nicht aktuell
     * @throws EmailExistsException Es gibt bereits einen Studenten mit der E-Mail-Adresse
     */
    @Transactional
    public Student update(final Student student, final UUID id, final int version) {
        log.debug("update: student={}", student);
        log.debug("update: id={}, version={}", id, version);

        var studentDb = repo
            .findById(id)
            .orElseThrow(() -> new NotFoundException(id));
        log.trace("update: version={}, studentDb={}", version, studentDb);
        if (version != studentDb.getVersion()) {
            throw new VersionOutdatedException(version);
        }

        final var email = student.getEmail();
        // Ist die neue E-Mail bei einem *ANDEREN* Studenten vorhanden?
        if (!Objects.equals(email, studentDb.getEmail()) && repo.existsByEmail(email)) {
            log.debug("update: email {} existiert", email);
            throw new EmailExistsException(email);
        }
        log.trace("update: Kein Konflikt mit der E-Mail-Adresse");

        // zu überschreibende Werte übernehmen
        studentDb.set(student);
        studentDb = repo.save(studentDb);

        log.debug("update: {}", studentDb);
        return studentDb;
    }

    /**
     * Einen Studenten löschen.
     *
     * @param id Die Id des zu löschenden Studenten
     */
    @Transactional
    public void deleteById(final UUID id) {
        log.debug("deleteById: id={}", id);
        final var student = repo.findById(id).orElse(null);
        if (student == null) {
            log.debug("deleteById: id={} nicht vorhanden", id);
            return;
        }
        repo.delete(student);
    }
}
