package com.acme.ttx.repository;


import com.acme.ttx.entity.Adresse;
import com.acme.ttx.entity.Student;
import lombok.NonNull;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.acme.ttx.entity.Student.ADRESSE_GRAPH;
import static com.acme.ttx.entity.Student.ADRESSE_GUTHABEN_GRAPH;


/**
 * Repository für den DB-Zugriff
 */
@Repository
public interface StudentRepository extends JpaRepository<Student, UUID>, JpaSpecificationExecutor<Student> {
    @EntityGraph(ADRESSE_GRAPH)
    @NonNull
    List<Student> findAll();

    @EntityGraph(ADRESSE_GRAPH)
    @NonNull
    @Override
    List<Student> findAll(@NonNull Specification<Student> spec);

    @EntityGraph(ADRESSE_GRAPH)
    @NonNull
    @Override
    Optional<Student> findById(@NonNull UUID id);

    /**
     * Student einschließlich Guthaben anhand der ID Suchen.
     *
     * @param id Student ID
     * @return gefundenen Studenten
     */
    @Query("""
        SELECT DISTINCT s
        FROM #{#entityName} s
        WHERE s.id = :id
    """)
    @EntityGraph(ADRESSE_GUTHABEN_GRAPH)
    @NonNull
    Optional<Student> findByIdFetchGuthaben(UUID id);

    /**
     * Student zu gegebener E-Mail-Adresse aus der DB ermitteln.
     *
     * @param email E-Mail-Adresse des gesuchten Studenten
     * @return Optional mit gefundenen Studenten oder leeres Optional
     */
    @Query("""
        SELECT s
        FROM #{#entityName} s
        WHERE lower(s.email) LIKE concat(lower(:email), '%')
    """)
    @EntityGraph(ADRESSE_GRAPH)
    Optional<Student> findByEmail(String email);

    /**
     * Abfrage, ob es einen Studenten mit gegebener E-Mail existiert.
     * @param email E-Mail-Adresse für die Suche
     * @return true, falls es solchen Studenten gibt, sonst false
     */
    @SuppressWarnings("BooleanMethodNameMustStartWithQuestion")
    boolean existsByEmail(String email);

    /**
     * Studenten anhand des Nachnamen suchen.
     *
     * @param nachname der (Teil-) Nachname des gesuchten Studenten
     * @return den gefundenen Studenten oder eine leere Collection
     */
    @Query("""
        SELECT DISTINCT s
        FROM #{#entityName} s
        WHERE lower(s.nachname) LIKE concat ('%', lower(:nachname), '%')
        GROUP BY s.nachname
        """)
    @EntityGraph(ADRESSE_GRAPH)
    List<Student> findByNachname(CharSequence nachname);

    @Query("""
        SELECT s.nachname
        FROM #{#entityName} s
        WHERE    lower(s.nachname) LIKE concat(lower(:prefix), '%')
        ORDER BY s.nachname
        """)
    List<String> findNachnamenByPrefix(String prefix);
}
