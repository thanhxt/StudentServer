package com.acme.ttx.rest;

import com.acme.ttx.entity.Adresse;
import com.acme.ttx.entity.Guthaben;
import com.acme.ttx.entity.ModuleType;
import com.acme.ttx.entity.SemesterType;
import com.acme.ttx.entity.Student;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDate;
import java.util.List;


@JsonPropertyOrder({
    "name", "nachname", "email", "geburtsdatum", "adresse",
    "guthaben", "semester", "module"
})
@Relation(collectionRelation = "studenten", itemRelation = "student")
@EqualsAndHashCode(onlyExplicitlyIncluded = true ,callSuper = false)
@Getter
@ToString(callSuper = true)
public class StudentModel  extends RepresentationModel<StudentModel> {
    private final String name;
    private final String nachname;
    private final String email;
    private final LocalDate geburtsdatum;
    private final Adresse adresse;
    private final List<Guthaben> guthaben;
    private final SemesterType semester;
    private final List<ModuleType> module;

    StudentModel(final Student student) {
        name = student.getName();
        nachname = student.getNachname();
        email = student.getEmail();
        geburtsdatum = student.getGeburtsdatum();
        adresse = student.getAdresse();
        guthaben = student.getGuthaben();
        semester = student.getSemester();
        module = student.getModule();
    }
}
