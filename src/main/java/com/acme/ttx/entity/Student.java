package com.acme.ttx.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.builder.ToStringExclude;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import static com.acme.ttx.entity.Student.ADRESSE_GRAPH;
import static com.acme.ttx.entity.Student.ADRESSE_GUTHABEN_GRAPH;
import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.CascadeType.REMOVE;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static java.util.Collections.emptyList;

/**
 * Daten eines Studenten.
 * <img src="../../../../../../../extras/doc/Student.png" alt="Klassendiagramm">
 *
 */

@Entity
@Table(name="student")
@NamedEntityGraph(name = ADRESSE_GRAPH, attributeNodes = @NamedAttributeNode("adresse"))
@NamedEntityGraph(name = ADRESSE_GUTHABEN_GRAPH, attributeNodes = {
    @NamedAttributeNode("adresse"), @NamedAttributeNode("guthaben")
})
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper=false)
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class Student {
     /**
      * NamedEntityGraph für das Attribut "adresse".
      */
    public static final String ADRESSE_GRAPH = "Student.adresse";

    /**
     * NamedEntityGraph für die Attribute "adresse" und "guthaben".
     */
    public static final String ADRESSE_GUTHABEN_GRAPH = "Student.adresseGuthaben";

    /**
     * Die ID des Studenten.
     */
    @Id
    @GeneratedValue
    @EqualsAndHashCode.Include
    private UUID id;

    /**
     * Versionsnummern für die optimistische Synchronisation.
     */
    @Version
    private int version;

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
     * Semester des Studenten.
     */
    @Enumerated(STRING)
    private SemesterType semester;

    /**
     * Die Adresse des Studenten.
     */

    @OneToOne(optional = false, cascade = {PERSIST, REMOVE}, fetch = LAZY, orphanRemoval = true)
    @ToString.Exclude
    private Adresse adresse;

     /**
     * Guthaben des Studenten.
     */
    @OneToMany(cascade = {PERSIST, REMOVE}, orphanRemoval = true)
    @JoinColumn(name = "student_id")
    @OrderColumn(name = "idx", nullable = false)
    @ToString.Exclude
    private List<Guthaben> guthaben;

    /**
     * Belegte Module des Studenten.
     */
    @Transient
    @ToStringExclude
    private List<ModuleType> module;

    @Column(name = "module")
    private String moduleStr;

    private String username;

    @CreationTimestamp
    private LocalDateTime erzeugt;

    @UpdateTimestamp
    private LocalDateTime aktualisiert;

    /**
     * Studentendateien überschreiben.
     *
     * @param student Neue Studentendaten.
     */
    public void set(final Student student) {
        nachname = student.nachname;
        name = student.name;
        email = student.email;
        geburtsdatum = student.geburtsdatum;
        semester = student.semester;
    }

    @PrePersist
    private void buildModuleStr() {
        if(module == null || module.isEmpty()) {
            moduleStr = null;
            return;
        }
        final var stringList = module.stream()
            .map(Enum::name)
            .toList();
        moduleStr = String.join(", ", stringList);
    }

    @PostLoad
    @SuppressWarnings("java:S6204")
    private void loadModuleStr(){
        if (moduleStr == null) {
            // NULL in der DB Spalte
            module = emptyList();
            return;
        }
        final var moduleArray = moduleStr.split(",");
        module = Arrays.stream(moduleArray)
            .map(ModuleType::valueOf)
            .collect(Collectors.toList());
    }
}
