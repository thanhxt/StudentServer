package com.acme.ttx.service;


import com.acme.ttx.entity.Student;
import com.acme.ttx.repository.SpecificationBuilder;
import com.acme.ttx.repository.StudentRepository;
import com.acme.ttx.security.Rolle;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.micrometer.observation.annotation.Observed;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static com.acme.ttx.security.Rolle.ADMIN;

/**
 * Anwendungslogik für den Studenten.
 * <img src="../../../../../../../extras/doc/StudentReadService.png" alt="Klassendiagramm">
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class StudentReadService {
    private final StudentRepository repo;
    private final SpecificationBuilder specificationBuilder;


    /**
     * Studenten anhand von Suchkriterien als Collection suchen.
     *
     * @param suchkriterien Die Suchkriterien
     * @return Die gefundenen Studenten oder eine leere Liste
     * @throws NotFoundException - falls keine Studenten gefunden wurden
     */
    @SuppressWarnings({"ReturnCount", "NestedIfDepth"})
    public Collection<Student> find(@NonNull final Map<String, List<String>> suchkriterien) {
        log.debug("find: suchkriterien={}", suchkriterien);

        if (suchkriterien.isEmpty()) {
            return repo.findAll();
        }

        if (suchkriterien.size() == 1) {
            final var nachnamen = suchkriterien.get("nachnamen");
            if (nachnamen != null && nachnamen.size() == 1) {
                return findByNachname(nachnamen.getFirst(), suchkriterien);
            }

            final var emails = suchkriterien.get("email");
            if (emails != null && emails.size() == 1) {
                return findByEmail(emails.getFirst(), suchkriterien);
            }
        }

        final var specification = specificationBuilder
            .build(suchkriterien)
            .orElseThrow(() -> new NotFoundException(suchkriterien));

        final var studenten = repo.findAll(specification);
        if (studenten.isEmpty()) {
            throw new NotFoundException(suchkriterien);
        }
        log.debug("find {}", studenten);
        return studenten;
    }

    @Observed(name = "find-By-id")
    public @NonNull Student findById(
        final UUID id,
        final String username,
        final List<Rolle> rollen,
        final boolean fetchGuthaben
        ) {
        log.debug("findById: id ={} username={}, rollen={}", id, username, rollen);

        final var studentOptional = fetchGuthaben ? repo.findByIdFetchGuthaben(id) : repo.findById(id);
        final var student = studentOptional.orElse(null);
        log.trace("findById: student ={}", student);

        // beide find()-Methoden liefern ein Optional
        if (student != null && student.getUsername().contentEquals(username)) {
            return student;
        }

        if (!rollen.contains(ADMIN)) {
            // nicht admin, aber keine eigene (oder keine Kundendaten)
            throw new AccessForbiddenException(rollen);
        }

        // admin: Studentendaten evtl. nicht gefunden
        if (student == null) {
            throw new NotFoundException(id);
        }

        log.debug("findById: student={}, guthaben={}", student, fetchGuthaben ? student.getGuthaben() : "N/A");
        return student;
    }


    public List<Student> findByNachname(final String nachname, final Map<String, List<String>> suchkriterien) {
        log.trace("findByNachname: nachname={}", nachname);
        final var studenten = repo.findByNachname(nachname);
        if (studenten.isEmpty()) {
            throw new NotFoundException(suchkriterien);
        }
        log.debug("findByNachname: {}", studenten);
        return studenten;
    }

    private Collection<Student> findByEmail(String email, final Map<String, List<String>> suchkriterien) {
        log.trace("findByEmail: {}", email);
        final var student = repo
            .findByEmail(email)
            .orElseThrow(() -> new NotFoundException(suchkriterien));
        final var studenten = List.of(student);
        log.debug("findByEmail: {}", studenten);
        return studenten;
    }


    public @NonNull List<String> findNachnamenByPrefix(final String prefix) {
        log.debug("findNachnamenByPrefix: {}", prefix);
        final var nachnamen = repo.findNachnamenByPrefix(prefix);
        if (nachnamen.isEmpty()) {
            //noispection NewExceptionWithoutArguments
            throw new NotFoundException();
        }
        log.debug("findNachnamenByPrefix: {}", nachnamen);
        return nachnamen;
    }
}
