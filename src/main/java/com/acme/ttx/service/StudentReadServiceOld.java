package com.acme.ttx.service;

import com.acme.ttx.entity.Student;
import com.acme.ttx.repository.StudentRepositoryOld;
import jakarta.transaction.Transactional;
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
 * <img src="../../../../../../../extras/doc/StudentReadService.png" alt="Klassendiagramm">
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class StudentReadServiceOld {

    private final StudentRepositoryOld repo;

    /**
     * Einen Studenten anhand seiner Id suchen.
     *
     * @param id Die Id des gesuchten Studenten
     * @return  Der gefundene Student
     * @throws NotFoundException Falls kein Student gefunden wurde
     */
    public @NotNull Student findStudentById(final UUID id) {
        log.debug("findById: id={}", id);
        final var student = repo.findById(id)
            .orElseThrow(() -> new NotFoundException(id));
        log.debug("findById: id={}", student);
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
