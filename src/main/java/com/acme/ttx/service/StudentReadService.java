package com.acme.ttx.service;

import com.acme.ttx.entity.Student;
import com.acme.ttx.repository.StudentRepository;
import jakarta.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Anwendungslogik für den Studenten.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StudentReadService {

    private final StudentRepository repo;

    /**
     * Einen Studenten anhand seiner Matrikelnummer suchen.
     *
     * @param matrikelnummer Die Matrikelnummer des gesuchten Studenten
     * @return  Der gefundene Student
     * @throws NotFoundException Falls kein Student gefunden wurde
     */
    public @NotNull Student findStudentByMatrikelnummer(final UUID matrikelnummer) {
        log.debug("findByMatrikelnummer: matrikelnummer={}", matrikelnummer);
        final var student = repo.findByMatrikelnummer(matrikelnummer)
            .orElseThrow(() -> new NotFoundException(matrikelnummer));
        log.debug("findByMatrikelnummer: matrikelnummer={}", student);
        return student;
    }

    /**
     * Studenten anhand von Suchkriterien als Collection suchen.
     *
     * @param suchkriterien Die Suchkriterien
     * @return Die gefundenen Studenten oder eine leere Liste
     * @throws NotFoundException - falls keine Studenten gefunden wurden
     */
    @SuppressWarnings({"ReturnCount", "NestedIfDepth"})
    public Collection<Student> find(final Map<String, List<String>> suchkriterien) {
        log.debug("find: suchkriterien={}", suchkriterien);

        if (suchkriterien.isEmpty()) {
            return repo.findAll();
        }

        if (suchkriterien.size() == 1) {
            final var nachnamen = suchkriterien.get("nachnamen");
            if (nachnamen != null && nachnamen.size() == 1) {
                final var student = repo.findByNachname(nachnamen.getFirst());
                if (student.isEmpty()) {
                    throw new NotFoundException(suchkriterien);
                }
                log.debug("find (nachname): {}", nachnamen);
                return student;
            }

            final var emails = suchkriterien.get("email");
            if (emails != null && emails.size() == 1) {
                final var student = repo
                    .findByEmail(emails.getFirst())
                    .orElseThrow(() -> new NotFoundException(suchkriterien));
                final var studenten = List.of(student);
                log.debug("find (email): {}", studenten);
                return studenten;
            }
        }

        final var studenten = repo.find(suchkriterien);
        if (studenten.isEmpty()) {
            throw new NotFoundException(suchkriterien);
        }

        log.debug("find {}", studenten);
        return studenten;
    }

    /**
     * Listet alle Studenten, auf die in der DB sind.
     *
     * @return  Alle Studenten
     */
    public @NotNull Collection<Student> findAllStudents() {
        return repo.findAll();
    }
}
