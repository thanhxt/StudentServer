package com.acme.ttx.rest;

import com.acme.ttx.entity.Adresse;
import com.acme.ttx.entity.Guthaben;
import com.acme.ttx.entity.Student;
import org.mapstruct.AnnotateWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import static org.mapstruct.NullValueMappingStrategy.RETURN_DEFAULT;

/**
 * Mapper zwischen Entity-Klassen. Siehe build\generated\sources\annotationProcessor\java\main\...\KundeMapperImpl.java.
 *
 */
@Mapper(nullValueIterableMappingStrategy = RETURN_DEFAULT, componentModel = "spring")
@AnnotateWith(ExcludeFromJacocoGeneratedReport.class)
interface StudentMapper {
    /**
     * Ein DTO-Objekt von StudentenDTO in ein Objekt für Student konvertieren.
     *
     * @param dto DTO-Objekt für StudentDTO ohne Matrikelnummer
     * @return Konvertiertes Studenten-Objekt mit null als Matrikelnummer
     */
    @Mapping(target = "matrikelnummer", ignore = true)
    Student toStudent(StudentDTO dto);

    /**
     * Ein DTO-Objekt von AdresseDTO in ein Objekt für Adresse konvertierten.
     *
     * @param dto DTO-Objekt für AdresseDTO ohne Student
     * @return Konvertiertes Adresse-Objekt
     */
    Adresse toAdresse(AdresseDTO dto);

    /**
     * Ein DTO-Objekt von GuthabenDTO in ein Objekt für Guthaben konvertieren.
     *
     * @param dto DTO-Objekt für GuthabenDTO ohne Student
     * @return Konvertiertes Guthaben-Objekt
     */
    Guthaben toGuthaben(GuthabenDTO dto);
}
